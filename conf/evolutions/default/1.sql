# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account (
  uid                       varchar(255) not null,
  email                     varchar(255),
  name                      varchar(255),
  linkedin_uid              varchar(255),
  headline                  varchar(255),
  industry                  varchar(255),
  constraint pk_account primary key (uid))
;

create table event (
  uid                       varchar(255) not null,
  status                    integer,
  content_uid               varchar(255),
  sender                    varchar(255),
  recepient                 varchar(255),
  journey_journey_id        varchar(255),
  constraint pk_event primary key (uid))
;

create table event_content (
  uid                       varchar(255) not null,
  type                      integer,
  timestamp                 bigint,
  text                      varchar(255),
  constraint pk_event_content primary key (uid))
;

create table journey (
  journey_id                varchar(255) not null,
  type                      integer,
  start_time                bigint,
  end_time                  bigint,
  previous_location_name    varchar(255),
  location_name             varchar(255),
  number                    varchar(255),
  seat                      varchar(255),
  railcar                   varchar(255),
  latitude                  float,
  longitude                 float,
  trip_trip_id              varchar(255),
  constraint pk_journey primary key (journey_id))
;

create table trip (
  trip_id                   varchar(255) not null,
  name                      varchar(255),
  image_url                 varchar(255),
  status                    integer,
  last_modified             bigint,
  traveler_uid              varchar(255),
  constraint pk_trip primary key (trip_id))
;

create sequence account_seq;

create sequence event_seq;

create sequence event_content_seq;

create sequence journey_seq;

create sequence trip_seq;

alter table event add constraint fk_event_content_1 foreign key (content_uid) references event_content (uid);
create index ix_event_content_1 on event (content_uid);
alter table event add constraint fk_event_journey_2 foreign key (journey_journey_id) references journey (journey_id);
create index ix_event_journey_2 on event (journey_journey_id);
alter table journey add constraint fk_journey_trip_3 foreign key (trip_trip_id) references trip (trip_id);
create index ix_journey_trip_3 on journey (trip_trip_id);
alter table trip add constraint fk_trip_traveler_4 foreign key (traveler_uid) references account (uid);
create index ix_trip_traveler_4 on trip (traveler_uid);



# --- !Downs

drop table if exists account cascade;

drop table if exists event cascade;

drop table if exists event_content cascade;

drop table if exists journey cascade;

drop table if exists trip cascade;

drop sequence if exists account_seq;

drop sequence if exists event_seq;

drop sequence if exists event_content_seq;

drop sequence if exists journey_seq;

drop sequence if exists trip_seq;

