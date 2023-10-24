create table if not exists products
(
    id    serial primary key,
    ean   varchar(15) unique not null,
    name  varchar(255)       not null,
    brand varchar(255)       not null
);

create table if not exists profiles
(
    id      uuid primary key,
    name    varchar(255)        not null,
    surname varchar(255)        not null,
    email   varchar(255) unique not null
);

create table if not exists purchases
(
    id           uuid primary key not null,
    profile_id   uuid,
    purchased_at timestamptz      not null,

    FOREIGN KEY (profile_id) REFERENCES profiles (id)
);

create table if not exists purchase_product
(
    purchase_id uuid not null,
    product_id  int  not null,

    PRIMARY KEY (purchase_id, product_id),
    FOREIGN KEY (purchase_id) REFERENCES purchases (id),
    FOREIGN KEY (product_id) REFERENCES products (id)
);

create table if not exists employees
(
    id         uuid primary key,
    username   varchar(255) unique not null,
    working_on int default 0
);

create table if not exists specializations
(
    id   serial primary key,
    name varchar(255) not null
);

create table if not exists tickets
(
    id                   serial primary key,
    status               varchar(15)  not null,
    title                varchar(255) not null,
    description          varchar(255) not null,
    customer_id          uuid         not null,
    expert_id            uuid,
    priority_level       int,
    product_id           int          not null,
    created_date         timestamptz  not null,
    closed_date          timestamptz,
    specialization_id    int          not null,
    resolved_description text,
    survey_id            int,

    FOREIGN KEY (customer_id) REFERENCES profiles (id),
    FOREIGN KEY (expert_id) REFERENCES employees (id),
    FOREIGN KEY (product_id) REFERENCES products (id),
    FOREIGN KEY (specialization_id) REFERENCES specializations (id),
    FOREIGN KEY (survey_id) REFERENCES surveys (id)
);

create table if not exists messages
(
    id               uuid primary key not null,
    text             text             not null,
    timestamp        timestamptz      not null,
    ticket_id        int              not null,
    is_from_customer bool             not null,

    FOREIGN KEY (ticket_id) REFERENCES tickets (id)
);

create table if not exists surveys
(
    id                serial primary key,
    ticket_id         int unique not null,
    service_valuation int        not null,
    professionality   int        not null,
    comment           text,

    FOREIGN KEY (ticket_id) REFERENCES tickets (id)
);

create table if not exists expert_specialization
(
    expert_id uuid not null,
    specs_id  int  not null,

    PRIMARY KEY (expert_id, specs_id),
    FOREIGN KEY (expert_id) REFERENCES employees (id),
    FOREIGN KEY (specs_id) REFERENCES specializations (id)
);

create table if not exists changes
(
    id          serial primary key,
    from_status varchar(15),
    to_status   varchar(15) not null,
    timestamp   timestamptz not null,
    ticket_id   int         not null,
    expert_id   uuid,

    FOREIGN KEY (ticket_id) REFERENCES tickets (id),
    FOREIGN KEY (expert_id) REFERENCES employees (id)
);

create table if not exists notifications
(
    id        uuid primary key not null,
    text      text             not null,
    receiver  uuid             not null,
    timestamp timestamptz      not null
);