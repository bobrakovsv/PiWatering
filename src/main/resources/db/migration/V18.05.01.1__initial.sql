
create schema wtr;

create sequence wtr.seq start with 5000;
create sequence wtr.log;


create table wtr.setting (
  id bigint not null default wtr.seq.nextval primary key,
  name varchar(500) not null,
  value varchar(1000),
  descr varchar(2000),
  constraint setting_uk1 unique (name)
);


create table wtr.type (
  id bigint not null default wtr.seq.nextval primary key,
  name varchar(500) not null,
  type_id bigint,
  code varchar(30),
  descr varchar(2000),
  constraint type_uk1 unique (code),
  constraint type_uk2 unique (type_id, name),
  constraint type_fk1 foreign key (type_id) references wtr.type(id)
);

create index wtr.type_idx1 on wtr.type(type_id);


create table wtr.hw_address (
  id bigint not null default wtr.seq.nextval primary key,
  type_id bigint,
  name varchar(500) not null,
  value varchar(30),
  descr varchar(2000),
  constraint hwadr_uk1 unique (name),
  constraint hwadr_uk2 unique (type_id, value),
  constraint hwadr_fk1 foreign key (type_id) references wtr.type(id)
);

create index wtr.hwadr_idx1 on wtr.hw_address(type_id);


create table wtr.polarity_switch (
  id bigint not null default wtr.seq.nextval primary key,
  name varchar(500),
  hwadr_id bigint not null,
  off_sig integer not null,
  on_sig integer not null,
  descr varchar(2000),

  constraint polswtch_uk1 unique (name),
  constraint polswtch_uk2 unique (hwadr_id),
  constraint polswtch_fk1 foreign key (hwadr_id) references wtr.hw_address(id)
);

create index wtr.polswtch_idx1 on wtr.polarity_switch(hwadr_id);


create table wtr.valve (
  id bigint not null default wtr.seq.nextval primary key,
  name varchar(500) not null,
  type_id bigint not null,
  polswtch_id bigint,
  hwadr_id bigint not null,
  off_sig integer not null,
  on_sig integer not null,
  auto_control boolean not null,
  open_condition_expr varchar(4000),
  open_duration_expr varchar(4000) not null,
  open_after_id bigint,
  descr varchar(1000),
  is_open boolean,
  state_date timestamp,
  next_state_date timestamp,

  constraint valve_uk1 unique (name),
  constraint valve_uk2 unique (hwadr_id),
  constraint valve_fk1 foreign key (type_id) references wtr.type(id),
  constraint valve_fk2 foreign key (hwadr_id) references wtr.hw_address(id),
  constraint valve_fk3 foreign key (open_after_id) references wtr.valve(id),
  constraint valve_fk4 foreign key (polswtch_id) references wtr.polarity_switch(id)
);

create index wtr.valve_idx1 on wtr.valve(type_id);
create index wtr.valve_idx2 on wtr.valve(hwadr_id);
create index wtr.valve_idx3 on wtr.valve(open_after_id);
create index wtr.valve_idx4 on wtr.valve(polswtch_id);


create table wtr.valve_state_hist (
  id bigint not null default wtr.log.nextval primary key,
  valve_id bigint not null,
  state_date timestamp not null,
  is_open boolean not null,
  constraint valvesth_uk1 unique (valve_id, state_date),
  constraint valvesth_fk1 foreign key (valve_id) references wtr.valve(id)
);

create index wtr.valvesth_idx1 on wtr.valve_state_hist(valve_id);


create table wtr.message (
	id bigint not null default wtr.log.nextval primary key,
	msg_date timestamp not null default systimestamp,
	text varchar(4000) not null,
	severity integer not null default 3,
	stack varchar(4000)
);

create index wtr.msg_idx1 on wtr.message(msg_date);


create table wtr.weather (
  id bigint not null default wtr.log.nextval primary key,
  qry_date timestamp not null,
  forecast boolean not null,
  start_date timestamp not null,
  end_date timestamp not null,
  temp_min integer,
  temp_max integer,
  rain double,
  wind_speed integer,
  wind_deg integer,
  clouds_pct integer,
  constraint weather_uk1 unique (forecast, start_date)
);


