@UP
alter table auth_session add column token_date timestamp;
update auth_session set token_date=(select current_timestamp);
alter table auth_session alter column token_date set not null;
@UP

@DOWN
alter table auth_session drop column token_date;
@DOWN