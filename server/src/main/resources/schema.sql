
create table if not exists products (
    ean varchar(15) primary key,
    name varchar(255) not null,
    brand varchar(255) not null
);

create table if not exists profiles(
    id serial primary key,
    name varchar(255) not null ,
    surname varchar(255) not null ,
    email varchar(255) unique not null
);