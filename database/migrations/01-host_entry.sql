@UP
create table host_entry (
	id serial primary key,			
	hostname varchar(100) not null,		-- the hostname 
	protocol varchar(10) not null,		-- the protocol (ftp, ftps, etc)
	username varchar(100) not null,		-- the username to connect with
	password varchar(100) not null,		-- the password to use
	max_connections integer not null,	-- the max connections we can have open concurrently
	timeout integer not null, 			-- the time to wait before giving up
	retry_wait integer not null,		-- how to long to wait between retry attempts
	retry_count integer not null,		-- how many times to retry
	version integer not null
);
alter table host_entry add constraint host_entry_uq unique (hostname, protocol);
@UP

@DOWN
drop table host_entry;
@DOWN