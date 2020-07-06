drop table if exists post;
create table post (
	id serial primary key,
	name varchar(500),
	text varchar(20000),
	link varchar(200) unique,
	created timestamp
);