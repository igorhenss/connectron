create table if not exists connectron."user" (
    id serial not null,
    created_at timestamp not null default current_timestamp,
    username varchar(60) not null,
    email varchar(80) not null,
    constraint user_pk primary key (id)
);
