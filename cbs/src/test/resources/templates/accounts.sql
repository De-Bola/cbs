-- Table: accounts
DROP TABLE IF EXISTS accounts CASCADE;
CREATE TABLE IF NOT EXISTS accounts
(
    account_id uuid NOT NULL ,
    customer_id VARCHAR (255),
    country VARCHAR (255),
    PRIMARY KEY (account_id)
);

insert into accounts (account_id, customer_id, country) values ('eb01ed99-59fc-48f2-8bcc-5f245e3a17bd', '3707739821', 'Syria');
insert into accounts (account_id, customer_id, country) values ('0607ed0a-6ab6-4544-9c1d-e4de2cd8258e', '3206497868', 'Philippines');
insert into accounts (account_id, customer_id, country) values ('7997cf2b-63eb-45d5-ba58-300568e7eb87', '9963939643', 'Ukraine');
insert into accounts (account_id, customer_id, country) values ('3eeaf1ab-e103-4e0a-b69f-2bce91e758be', '1426954859', 'China');
insert into accounts (account_id, customer_id, country) values ('c3fabe6c-d5d9-40f8-b575-51bf1042e963', '1385771879', 'China');
insert into accounts (account_id, customer_id, country) values ('422dd1e4-911c-465c-a16a-4e80a4eac45c', '3092960955', 'United States');
insert into accounts (account_id, customer_id, country) values ('8a6fca7d-ebef-499a-831c-893693bb4f16', '5574891301', 'Thailand');
insert into accounts (account_id, customer_id, country) values ('d583895e-a953-411e-a821-ce7ed8002255', '3317677155', 'Japan');
insert into accounts (account_id, customer_id, country) values ('9dd3f778-11b4-460f-9301-ec9763e1fcbb', '2571352040', 'Poland');
insert into accounts (account_id, customer_id, country) values ('58c8b9c9-a217-42c3-b219-a6a225b50469', '2010503813', 'Sweden');
