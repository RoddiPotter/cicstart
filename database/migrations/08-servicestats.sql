@UP
create table service_stats (
	id serial primary key,			
	service_name varchar(10) not null,	-- the name of the service (auth, catalogue, file, macro, etc.)
	invocations int not null,			-- the number of invocations since last reset
	reset_date timestamp not null,		-- the date/time when the invocation counter was last reset
	version integer not null
);
alter table service_stats add constraint uq_service_stats unique (service_name);
@UP

@DOWN
drop table service_stats;
@DOWN