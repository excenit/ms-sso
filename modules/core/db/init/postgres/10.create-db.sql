-- begin SEC_USER
alter table SEC_USER add column MICROSOFT_ID varchar(255) ^
alter table SEC_USER add column DTYPE varchar(31) ^
update SEC_USER set DTYPE = 'sso_SsoUser' where DTYPE is null ^
-- end SEC_USER
