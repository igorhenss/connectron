create table if not exists connectron."mapping" (
    id serial not null,
    created_at timestamp not null default current_timestamp,
    user_id integer not null,
    "value" varchar(5120) not null,
    constraint mapping_pk primary key (id),
    constraint mapping_user_fk foreign key (user_id) references connectron."user"(id)
);
