/* ============================================================
 * CatalogueService.java
 * ============================================================
 * Copyright 2013 University of Alberta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ 
 */

package ca.ualberta.physics.cssdp.catalogue.service;

import static com.jayway.restassured.RestAssured.given;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.EvalError;
import bsh.Interpreter;
import ca.ualberta.physics.cssdp.catalogue.dao.DataProductDao;
import ca.ualberta.physics.cssdp.catalogue.dao.ProjectDao;
import ca.ualberta.physics.cssdp.catalogue.dao.UrlDataProductDao;
import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.domain.catalogue.DataProduct;
import ca.ualberta.physics.cssdp.domain.catalogue.Discriminator;
import ca.ualberta.physics.cssdp.domain.catalogue.Project;
import ca.ualberta.physics.cssdp.domain.catalogue.UrlDataProduct;
import ca.ualberta.physics.cssdp.domain.catalogue.UrlDataProductUpdateMap;
import ca.ualberta.physics.cssdp.domain.file.DirectoryListing;
import ca.ualberta.physics.cssdp.domain.file.RemoteFile;
import ca.ualberta.physics.cssdp.model.Mnemonic;
import ca.ualberta.physics.cssdp.service.ManualTransaction;
import ca.ualberta.physics.cssdp.service.ServiceResponse;
import ca.ualberta.physics.cssdp.util.UrlParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.jayway.restassured.response.Response;

public class CatalogueService {

	private static final Logger logger = LoggerFactory
			.getLogger(CatalogueService.class);

	@Inject
	private ProjectDao projectDao;

	@Inject
	private DataProductDao dataProductDao;

	@Inject
	private UrlDataProductDao urlDataProductDao;

	@Inject
	private EntityManager em;

	@Inject
	private ObjectMapper mapper;

	// private final AtomicInteger requestIdGenerator = new AtomicInteger();

	public ServiceResponse<Void> create(final Project project) {

		final ServiceResponse<Void> sr = new ServiceResponse<Void>();

		new ManualTransaction(sr, em) {

			@Override
			public void onError(Exception e, ServiceResponse<?> sr) {
				sr.error(e.getMessage());
			}

			@Override
			public void doInTransaction() {

				// is there already a project with this external key?
				Project existing = projectDao.find(project.getExternalKey());

				// yes, please update
				if (existing != null) {
					sr.error("There is already a project with external key = "
							+ project.getExternalKey()
							+ ".  Please use a different key or update this project.");
				} else {

					// no, validate & create
					projectDao.save(project);

				}

			}
		};
		return sr;
	}

	public ServiceResponse<Project> find(String projectExternalKey) {
		ServiceResponse<Project> sr = new ServiceResponse<Project>();
		try {
			Mnemonic externalKey = new Mnemonic(projectExternalKey);
			Project project = projectDao.find(externalKey);
			sr.setPayload(project);
		} catch (IllegalArgumentException e) {
			sr.error(e.getMessage());
		}
		return sr;
	}

	public ServiceResponse<Void> delete(final Project project) {
		ServiceResponse<Void> sr = new ServiceResponse<Void>();

		new ManualTransaction(sr, em) {

			@Override
			public void onError(Exception e, ServiceResponse<?> sr) {
				sr.error(e.getMessage());
			}

			@Override
			public void doInTransaction() {
				projectDao.delete(project);
			}
		};

		return sr;
	}

	public ServiceResponse<List<Project>> findAll() {
		ServiceResponse<List<Project>> sr = new ServiceResponse<List<Project>>();
		List<Project> projects = projectDao.findAll();
		sr.setPayload(projects);
		return sr;
	}

	public ServiceResponse<List<URI>> find(Mnemonic projectExtKey,
			List<Mnemonic> observatoryExtKeys,
			List<Mnemonic> instrumentTypeExtKeys, Mnemonic discriminatorExtKey,
			LocalDateTime start, LocalDateTime end) {

		ServiceResponse<List<URI>> sr = new ServiceResponse<List<URI>>();

		Project project = projectDao.find(projectExtKey);

		logger.debug("found project " + project.getName());

		// can get the discriminator directly from the project
		Discriminator discriminator = null;
		if (discriminatorExtKey != null) {
			discriminator = project.getDiscriminator(discriminatorExtKey);
		}

		// find the data products for all these bits of criteria
		List<DataProduct> dataProducts = dataProductDao.find(project,
				observatoryExtKeys, instrumentTypeExtKeys, discriminator);

		logger.debug("found data products " + Joiner.on("; ").join(dataProducts));

		// and finally, query for the urls.
		List<URI> result = urlDataProductDao.findUrls(dataProducts, start, end);

		sr.setPayload(result);

		return sr;
	}

	public ServiceResponse<Void> scan(Project project, String sessionToken) {

		ServiceResponse<Void> sr = new ServiceResponse<Void>();
		List<String> roots = project.getScanDirectories();
		String host = project.getHost();
		String fileUrl = Common.properties().getString("file.api.url");
		String hostResource = fileUrl + "/host.json";

		List<UrlDataProduct> unsavedUrlDataProducts = new ArrayList<UrlDataProduct>();

		for (String root : roots) {

			ServiceResponse<List<UrlDataProduct>> processSr = processDirectory(
					host, hostResource, project, root, sessionToken);

			if (!processSr.isRequestOk()) {
				sr.addMessages(processSr.getMessages());
				break;
			} else {
				unsavedUrlDataProducts.addAll(processSr.getPayload());
			}

		}

		urlDataProductDao.process(new UrlDataProductUpdateMap(
				unsavedUrlDataProducts));

		return sr;
	}

	private ServiceResponse<List<UrlDataProduct>> processDirectory(String host,
			String hostResource, Project project, String directory,
			String sessionToken) {

		UrlDataProductMapper urlDataProductMapper = new UrlDataProductMapper(
				project);

		ServiceResponse<List<UrlDataProduct>> sr = new ServiceResponse<List<UrlDataProduct>>(
				new ArrayList<UrlDataProduct>());

		String path = hostResource + "/{host}/ls";
		logger.debug("Scanning path: " + path + ", host=" + host);
		Response res = given().header("CICSTART.session", sessionToken).and()
				.queryParameter("path", directory).and()
				.queryParam("depth", "5").get(path, host);

		int statusCode = res.getStatusCode();

		if (statusCode == 200) {

			try {

				// logger.debug(res.asString());

				DirectoryListing ls = mapper.readValue(res.asByteArray(),
						DirectoryListing.class);

				for (RemoteFile entry : ls.getRemoteFiles()) {

					if (entry.isDir()) {

						ServiceResponse<List<UrlDataProduct>> subSr = processDirectory(
								host, hostResource, project,
								UrlParser.getPath(entry.getUrl()), sessionToken);

						sr.getPayload().addAll(subSr.getPayload());
						sr.addMessages(subSr.getMessages());

					} else {

						UrlDataProduct urlDataProduct = urlDataProductMapper
								.map(entry);
						if (urlDataProduct != null) {
							sr.getPayload().add(urlDataProduct);
						}

					}

				}

			} catch (Exception e) {
				logger.error("Could not read DirectoryListing", e);
				sr.error("Could not read DirectoryListing because "
						+ e.getMessage());
			}

		} else if (statusCode == 204) {
			// no data, end recursion
		} else {
			sr.error(res.asString());
		}
		return sr;
	}

	private class UrlDataProductMapper {

		private final Project project;

		public UrlDataProductMapper(Project project) {
			this.project = project;
		}

		private UrlDataProduct map(RemoteFile remoteFile) {

			UrlDataProduct urlDataProduct = null;

			String url = remoteFile.getUrl();

			boolean unmapped = true;

			for (DataProduct dataProduct : project.getDataProducts()) {

				if (dataProduct.shouldExclude(url)) {
					// this url is explicitly excluded from being mapped
					// logger.debug("EXCLUDED: " + url);
					unmapped = false;
					break;

				} else if (dataProduct.shouldInclude(url)) {

					// logger.debug("INCLUDED: " + url);
					urlDataProduct = map(url, dataProduct);
					if (urlDataProduct != null) {

					} else {
						logger.error("There was a problem mapping the url and data product, "
								+ "check the BeanShell and Regular Expressions!");
					}
					unmapped = false;
					break;

				}

			}

			if (unmapped) {
				logger.debug("UNMAPPED! Consider excluding instead: " + url);
			}

			return urlDataProduct;
		}

		private UrlDataProduct map(String url, DataProduct dataProduct) {

			UrlDataProduct urlDataProduct = new UrlDataProduct();
			urlDataProduct.setUrl(url);
			urlDataProduct.setDataProduct(dataProduct);
			urlDataProduct.setScanTimestamp(new LocalDateTime());

			String startDateRegex = getStartDateRegex(dataProduct);
			String startDateBeanShell = getStartDateBeanShell(dataProduct);

			String endDateRegex = getEndDateRegex(dataProduct);
			String endDateBeanShell = getEndDateBeanShell(dataProduct);

			LocalDateTime startDate = parseTimestamp(url, startDateRegex,
					startDateBeanShell);
			if (startDate != null) {
				urlDataProduct.setStartTimestamp(startDate);
			}

			LocalDateTime endDate = parseTimestamp(url, endDateRegex,
					endDateBeanShell);
			if (endDate != null) {
				urlDataProduct.setEndTimestamp(endDate);
			}
			return urlDataProduct;
		}

		public LocalDateTime parseTimestamp(String url, String sRegex,
				String beanShell) {

			LocalDateTime timestamp = null;

			String sDateTime;
			if (!Strings.isNullOrEmpty(sRegex)) {

				Pattern regex = Pattern.compile(sRegex);
				Matcher matcher = regex.matcher(url);

				if (matcher.find()) {
					sDateTime = matcher.group();

					if (Strings.isNullOrEmpty(sDateTime)) {
						logger.error("DateTime regex is bad, "
								+ "no date/time found in " + url + " using "
								+ sRegex);
					} else {

						Object result = null;

						try {

							Interpreter i = new Interpreter();
							i.eval(beanShell);

							i.set("url", url);
							i.set("regexResult", sDateTime);
							result = i.eval("parse(url, regexResult)");

							logger.trace("result=" + result);

						} catch (EvalError e) {
							logger.error("url=" + url + ", regexResult="
									+ sDateTime, e);
						}

						timestamp = (LocalDateTime) result;
					}
				}

			}
			return timestamp;
		}

		private String getStartDateBeanShell(DataProduct dataProduct) {
			/*
			 * Gets the data product specific value, and if not set, uses the
			 * project default value.
			 */
			String startDateBeanShell = dataProduct.getMetadataParserConfig()
					.getStartDateBeanShell();
			if (Strings.isNullOrEmpty(startDateBeanShell)) {
				startDateBeanShell = project.getStartDateBeanShell();
			}
			return startDateBeanShell;
		}

		private String getEndDateBeanShell(DataProduct dataProduct) {
			/*
			 * Gets the data product specific value, and if not set, uses the
			 * project default value.
			 */
			String endDateBeanShell = dataProduct.getMetadataParserConfig()
					.getEndDateBeanShell();
			if (Strings.isNullOrEmpty(endDateBeanShell)) {
				endDateBeanShell = project.getEndDateBeanShell();
			}
			return endDateBeanShell;
		}

		private String getStartDateRegex(DataProduct dataProduct) {

			/*
			 * Gets the data product specific value, and if not set, uses the
			 * project default value.
			 */
			String startDateRegex = dataProduct.getMetadataParserConfig()
					.getStartDateRegex();
			if (Strings.isNullOrEmpty(startDateRegex)) {
				startDateRegex = project.getStartDateRegex();
			}
			return startDateRegex;
		}

		private String getEndDateRegex(DataProduct dataProduct) {

			/*
			 * Gets the data product specific value, and if not set, uses the
			 * project default value.
			 */
			String endDateRegex = dataProduct.getMetadataParserConfig()
					.getEndDateRegex();
			if (Strings.isNullOrEmpty(endDateRegex)) {
				endDateRegex = project.getEndDateRegex();
			}
			return endDateRegex;
		}
	}
}
