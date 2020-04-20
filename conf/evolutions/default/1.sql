# --- !Ups

create table "users" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "firstName" varchar not null,
  "lastName" varchar not null,
  "age" int not null,
  "capacity" int not null
);

# --- !Downs

drop table "users" if exists;