create table post (
	id serial primary key,
	name varchar(200),
	text varchar(5000),
	link varchar(200) unique,
	created date
);