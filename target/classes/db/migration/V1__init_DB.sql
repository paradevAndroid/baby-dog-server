create sequence hibernate_sequence start 1 increment 1;
create table address (
       id  serial not null,
        apartment varchar(255),
        city varchar(255),
        country varchar(255),
        district varchar(255),
        house varchar(255),
        private_house boolean,
        street varchar(255),
        primary key (id)
);

create table client (
       id int4 not null,
        account_enabled boolean not null,
        birthday varchar(255),
        contact_phone1 varchar(255),
        email varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        photo text,
        photo_mini text,
        token_id varchar(255),
        type varchar(255),
        address_id int4,
        primary key (id)
);

create table pet (
        id  serial not null,
        add_info varchar(255),
        age_val int4 not null,
        breed varchar(255),
        gender boolean not null,
        is_sterilized boolean not null,
        is_vaccinated boolean not null,
        nickname varchar(255),
        puppy boolean not null,
        size_of_dog int4 not null,
        client_id int4,
        primary key (id)
);

create table promo_code (
        id  serial not null,
        phone varchar(255),
        promo_code varchar(255),
        primary key (id)
);

create table review (
        id  bigserial not null,
        author varchar(255),
        comment varchar(255),
        mark int4,
        client_id int4,
        worker_id int4,
        primary key (id)
);

create table schedule (
        id int8 not null,
        day_of_month int4,
        hour_of_day int4,
        minute int4,
        month int4,
        second int4,
        year int4,
        worker_full_busy_id int4,
        worker_not_full_busy_id int4,
        primary key (id)
);

 create table worker (
        id int4 not null,
        account_enabled boolean not null,
        birthday varchar(255),
        contact_phone1 varchar(255),
        email varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        photo text,
        photo_mini text,
        token_id varchar(255),
        type varchar(255),
        average_rating float8,
        can_give_medicine boolean not null,
        can_make_injection boolean not null,
        contact_phone2 varchar(255),
        count_of_pets int4,
        document1 text,
        document2 text,
        father_worker_id int4,
        has_childrens boolean not null,
        have_animals boolean not null,
        is_dog_handler boolean not null,
        price int4 not null,
        price_period int4,
        repeating_orders int4,
        size_of_dog int4 not null,
        skills varchar(255),
        take_pups boolean not null,
        address_id int4,
        primary key (id)
);

alter table if exists client
       add constraint address_fk
       foreign key (address_id)
       references address;

alter table if exists pet
       add constraint client_fk
       foreign key (client_id)
       references client;

alter table if exists review
       add constraint client_fk
       foreign key (client_id)
       references client;

alter table if exists review
       add constraint worker_fk
       foreign key (worker_id)
       references worker;

alter table if exists schedule
       add constraint worker_fk
       foreign key (worker_full_busy_id)
       references worker;

 alter table if exists schedule
       add constraint schedule_fk
       foreign key (worker_not_full_busy_id)
       references worker;


alter table if exists worker
       add constraint address_fk
       foreign key (address_id)
       references address;