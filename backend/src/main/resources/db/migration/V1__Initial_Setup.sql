CREATE TABLE customer (
    id BIGSERIAL primary key ,
    name TEXT not null ,
    email text not null unique ,
    password text not null ,
    gender text not null ,
    age int not null
);

-- alter table customer
--     add constraint customer_email_unique unique (email);

-- alter table customer
--     add column gender TEXT NOT NULL;