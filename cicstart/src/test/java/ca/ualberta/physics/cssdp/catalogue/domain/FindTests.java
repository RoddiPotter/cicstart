package ca.ualberta.physics.cssdp.catalogue.domain;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.nio.charset.Charset;

import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.ualberta.physics.cssdp.catalogue.dao.UrlDataProductDao;
import ca.ualberta.physics.cssdp.catalogue.resource.CatalogueTestsScaffolding;
import ca.ualberta.physics.cssdp.catalogue.service.CatalogueService;
import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.dao.EntityManagerProvider;
import ca.ualberta.physics.cssdp.domain.catalogue.CatalogueSearchResponse;
import ca.ualberta.physics.cssdp.domain.catalogue.DataProduct;
import ca.ualberta.physics.cssdp.domain.catalogue.Observatory;
import ca.ualberta.physics.cssdp.domain.catalogue.Project;
import ca.ualberta.physics.cssdp.domain.catalogue.UrlDataProduct;
import ca.ualberta.physics.cssdp.model.Mnemonic;
import ca.ualberta.physics.cssdp.service.ManualTransaction;
import ca.ualberta.physics.cssdp.service.ServiceResponse;

import com.google.common.io.Files;
import com.google.inject.Inject;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

public class FindTests extends CatalogueTestsScaffolding {

	@Inject
	private CatalogueService service;

	@Inject
	private UrlDataProductDao dao;

	@Inject
	private EntityManagerProvider emp;

	public FindTests() {
		InjectorHolder.inject(this);
	}

	@Before
	public void setupTestData() {

		Project maccs = new Project();
		maccs.setName("MACCS");
		maccs.setHost("space.augsburg.edu");
		maccs.setExternalKey(Mnemonic.of("MACCS"));

		Observatory pg = new Observatory();
		pg.setExternalKey(Mnemonic.of("PG"));
		pg.setProject(maccs);
		maccs.getObservatories().add(pg);

		DataProduct pg10sec = new DataProduct();
		pg10sec.setExternalKey(Mnemonic.of("PG10SEC"));
		pg10sec.setProject(maccs);
		pg10sec.addObservatory(pg);
		maccs.getDataProducts().add(pg10sec);

		ServiceResponse<Void> sr = service.create(maccs);

		Assert.assertTrue(sr.isRequestOk());

		// add some url dataproducts

		final UrlDataProduct url1 = new UrlDataProduct();
		url1.setDataProduct(pg10sec);
		url1.setUrl("url1");
		url1.setScanTimestamp(LocalDateTime.now());

		final UrlDataProduct url2 = new UrlDataProduct();
		url2.setDataProduct(pg10sec);
		url2.setUrl("url2");
		url2.setScanTimestamp(LocalDateTime.now());

		final UrlDataProduct url3 = new UrlDataProduct();
		url3.setDataProduct(pg10sec);
		url3.setUrl("url3");
		url3.setScanTimestamp(LocalDateTime.now());

		final UrlDataProduct url4 = new UrlDataProduct();
		url4.setDataProduct(pg10sec);
		url4.setUrl("url4");
		url4.setScanTimestamp(LocalDateTime.now());

		new ManualTransaction(new ServiceResponse<Void>(), emp.get()) {

			@Override
			public void onError(Exception e, ServiceResponse<?> sr) {

			}

			@Override
			public void doInTransaction() {
				dao.save(url1);
				dao.save(url2);
				dao.save(url3);
				dao.save(url4);
			}
		};

	}

	@Test
	public void testFind() throws Exception {

		// CatalogueSearchRequest searchRequest = new CatalogueSearchRequest();
		// searchRequest.setProjectKey(Mnemonic.of("MACCS"));
		// searchRequest.getObservatoryKeys().add(Mnemonic.of("PG"));
		//
		// System.out.println(mapper.writeValueAsString(searchRequest));

		Response res = given()
				.body(Files.toString(new File("../examples/findMACCS.json"),
						Charset.forName("UTF-8"))).and()
				.contentType(ContentType.JSON)
				.post(ResourceUrls.PROJECT + "/find");

		// System.out.println(res.asString());

		Assert.assertEquals(4, res.as(CatalogueSearchResponse.class).getUris()
				.size());

	}

}
