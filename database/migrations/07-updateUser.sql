@UP
alter table auth_user add column openstack_username varchar(100);
alter table auth_user add column openstack_password varchar(100);
alter table auth_user add column openstack_keyname varchar(100);
@UP

@DOWN
alter table auth_user drop column openstack_username;
alter table auth_user drop column openstack_password;
alter table auth_user drop column openstack_keyname;
@DOWN