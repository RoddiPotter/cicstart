@UP
-- defines a Project like SuperDARN
create table catalogue_project (
	id serial primary key,			
	ext_key varchar(50) not null,			-- the external key
	name varchar(1024) not null,			-- the name of this project
	url varchar(1024),						-- the project url
	host varchar(1024) not null,			-- the host where the data is stored
	rules_url varchar(1024),				-- the url to the rules of the road document
	start_date_regex varchar(1024),			-- a regular expression to parse start date from urls
	end_date_regex varchar(1024),			-- a regular expression to parse end date from urls
	excludes_regex varchar(1024),			-- a regular expression to exclude files
	start_date_bean_shell text,				-- the default start date bean shell script to use
	end_date_bean_shell text,				-- the default end date bean shell script to use
	version integer not null
);
alter table catalogue_project add constraint uq_catalogue_project unique (ext_key);

create table catalogue_scandirectories (
	project_id integer not null,			-- the project
	directory varchar(2048) not null		-- the directory
);
alter table catalogue_scandirectories add constraint fk_catalogue_scandirectories_project foreign key (project_id) references catalogue_project (id);


-- one to one with catalogue_data_product
create table catalogue_metadataparserconfig (
	id serial primary key,
	includes_regex varchar(1024),				-- a regular expression to include files
	start_date_regex varchar(1024),				-- a regular expression to parse start date from urls
	end_date_regex varchar(1024),				-- a regular expression to parse end date from urls
	start_date_bean_shell text,					-- a beanshell code snippet to parse the start date and transform it into a LocalDateTime object (runs after regex)
	end_date_bean_shell text,					-- a beanshell code snippet to parse the end date and transform it into a LocalDateTime object (runs after regex).  Also, might just be the start date + hours, minutes, seconds.
	version integer not null
);

-- defines Data Products such as SuperDARN fitacf data from Rankin Inlet
create table catalogue_dataproduct (
	id serial primary key,
	ext_key varchar(50) not null,			-- the external key
	project_id integer not null,			-- the project this data product is for
	description varchar(1024),				-- the description of this data product
	metadataparserconfig_id integer,		-- the parsing configuration for this data product
	version integer not null
);
alter table catalogue_dataproduct add constraint fk_catalogue_dataproduct_project foreign key (project_id) references catalogue_project (id);
alter table catalogue_dataproduct add constraint fk_catalogue_dataproduct_metadataparserconfig foreign key (metadataparserconfig_id) references catalogue_metadataparserconfig (id);
alter table catalogue_dataproduct add constraint uq_catalogue_dataproduct unique (ext_key,project_id);

-- defines observatories, such as the SuperDARN rnk (Rankin Inlet)
create table catalogue_observatory (
	id serial primary key,
	ext_key varchar(50) not null,			-- the external key
	project_id integer not null,			-- the project this observatory is for
	location point,							-- the lat,long of this observatory
	description varchar(1024),				-- the description of this observatory record
	version integer not null
);
alter table catalogue_observatory add constraint fk_catalogue_observatory_project foreign key (project_id) references catalogue_project (id);
alter table catalogue_observatory add constraint uq_catalogue_observatory unique (ext_key,project_id);

-- defines instrument types, such as a SuperDARN radar
create table catalogue_instrumenttype (
	id serial primary key,
	ext_key varchar(50) not null,			-- the external key
	project_id integer not null,			-- the project this instrument type is for
	description varchar(1024),				-- the description of this instrument type record
	version integer not null
);
alter table catalogue_instrumenttype add constraint fk_catalogue_instrumenttype_project foreign key (project_id) references catalogue_project (id);
alter table catalogue_instrumenttype add constraint uq_catalogue_instrumenttype unique (ext_key,project_id);

-- defines discriminators, such as fit vs fitacf
create table catalogue_discriminator (
	id serial primary key,
	ext_key varchar(50) not null,			-- the external key
	project_id integer not null,			-- the project this discriminator is for
	description varchar(1024),				-- the description of this discriminator record
	version integer not null
);
alter table catalogue_discriminator add constraint fk_catalogue_discriminator_project foreign key (project_id) references catalogue_project (id);
alter table catalogue_discriminator add constraint uq_catalogue_discriminator unique (ext_key,project_id);

-- a link table to specify which observatories describe the data product
create table catalogue_dataproduct_observatory (
	dataproduct_id integer not null,
	observatory_id integer not null
);

-- a link table to specify which instrument types describe the data product
create table catalogue_dataproduct_instrumenttype (
	dataproduct_id integer not null,
	instrumenttype_id integer not null
);

-- a link table to specify which discrimniator describes the data product (there is only one discriminator allowed per data product)
create table catalogue_dataproduct_discriminator (
	dataproduct_id integer not null,
	discriminator_id integer not null
);
alter table catalogue_dataproduct_discriminator add constraint uq_dataproduct_discriminator unique (dataproduct_id);

-- a group of observatories, like all observatories at Rankin Inlet or all observatories on the Churchill Line
create table catalogue_observatorygroup (
	id serial primary key,
	ext_key varchar(50) not null,			-- the external key
	description varchar(1024),				-- the description of this observatory record
	version integer not null
);
alter table catalogue_observatorygroup add constraint uq_catalogue_observatorygroup unique (ext_key);

-- the members of each observatory group
create table catalogue_observatorygroup_members (
	observatorygroup_id integer not null,	-- the observatory group
	observatory_id integer not null			-- the observatory
);
alter table catalogue_observatorygroup_members add constraint pk_catalogueobservatorygroup_members primary key (observatorygroup_id,observatory_id);

-- The mapping of urls to data products
create table catalogue_url_dataproduct (
	id bigserial primary key,
	dataproduct_id integer not null,		-- the data product this url is for
	url varchar(2048) not null,				-- the url of the file (unique)
	start_tstamp timestamp,					-- the start timestamp of the data in the file
	end_tstamp timestamp,					-- the end timestamp of the data in the file 
	scan_tstamp timestamp not null,			-- when CSSDP scanned the url and created or updated this record
	deleted boolean not null,				-- true if this file is marked as deleted
	version integer not null
);
--create unique index idx_catalogue_url_dataproduct ON catalogue_url_dataproduct (url);

@UP

@DOWN
drop table catalogue_url_dataproduct;
drop table catalogue_observatorygroup_members;
drop table catalogue_observatorygroup;
drop table catalogue_dataproduct_discriminator;
drop table catalogue_dataproduct_instrumenttype;
drop table catalogue_dataproduct_observatory;
drop table catalogue_scandirectories;
drop table catalogue_discriminator;
drop table catalogue_instrumenttype;
drop table catalogue_observatory;
drop table catalogue_dataproduct;
drop table catalogue_metadataparserconfig;
drop table catalogue_project;
@DOWN