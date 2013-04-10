@UP
create table auth_session (
	id serial primary key,			
	user_id integer not null,			-- the user this session belongs to
	token varchar(36) not null,			-- the session token
	version integer not null
);
alter table auth_session add constraint uq_auth_session unique (token);
@UP

@DOWN
drop table auth_session;
@DOWN