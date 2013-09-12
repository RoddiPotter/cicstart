@UP
create table cached_file (
	id serial primary key,			
	local_path varchar(1256) not null,		-- the local path on disk where the file is cached
	size integer not null,					-- the size of the file in bytes
	md5 varchar(32) not null,				-- the md5 of the file
	file_tstamp timestamp not null,			-- the timestamp of the file (create timestamp)
	last_accessed timestamp not null,		-- the timestamp of when this file was last accessed
	file_name varchar(1024) not null,       -- the original name of the file
	version integer not null
);

create table cached_file_keys (
	cached_file_id integer not null,		-- the cached file this key is for
	ext_key varchar(1024) not null			-- the external key given to this file (must be unique)
);
alter table cached_file_keys add constraint external_key_uq unique (ext_key);
alter table cached_file_keys add constraint fk_keys_cached_file foreign key (cached_file_id) references cached_file (id); 
@UP

@DOWN
drop table cached_file_keys;
drop table cached_file;
@DOWN