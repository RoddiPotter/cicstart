@UP
create table auth_user (
	id serial primary key,			
	name varchar(100) not null,				-- the users name
	email varchar(100) not null,			-- the email address of this user
	password_digest varchar(32) not null,	-- the md5 hash of the users password
	password_salt varchar(32) not null, 	-- the salt for the password digest
	role varchar(15) not null,				-- the users role
	institution varchar(100),				-- the institution this user is at
	country varchar(100),					-- the country this user is at
	deleted boolean not null,				-- indicates if this user has been deleted
	version integer not null
);
alter table auth_user add constraint email_uq unique (email);
@UP

@DOWN
drop table auth_user;
@DOWN