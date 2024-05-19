truncate table tbl_book restart identity cascade;
truncate table tbl_author restart identity cascade;
truncate table tbl_genre restart identity cascade;
truncate table tbl_publisher restart identity cascade;

insert into tbl_genre (genre_id, genre_name, sort_order, valid_start_date, valid_end_date, created_by, updated_by)
values
(1, 'IT', 1, '2020-10-01 12:00:00', '9999-12-31 23:59:59', 'test', 'test'),
(2, '文学', 2, '2020-10-01 12:00:00', '9999-12-31 23:59:59', 'test', 'test'),
(3, '芸術', 3, '2020-10-01 12:00:00', '9999-12-31 23:59:59', 'test', 'test'),
(4, 'スポーツ', 4, '2020-10-01 12:00:00', '9999-12-31 23:59:59', 'test', 'test'),
(5, 'ゲーム', 5, '2020-10-01 12:00:00', '9999-12-31 23:59:59', 'test', 'test'),
(6, 'アニメ', 6, '2020-10-01 12:00:00', '9999-12-31 23:59:59', 'test', 'test'),
(7, '政治', 7, '2020-10-01 12:00:00', '9999-12-31 23:59:59', 'test', 'test'),
(8, '経済', 8, '2020-10-01 12:00:00', '9999-12-31 23:59:59', 'test', 'test');

truncate table tbl_publisher restart identity cascade;
insert into tbl_publisher(publisher_id, publisher_name, sort_order, valid_start_date, valid_end_date, created_by, updated_by)
values
(1, 'A社', 1, '2020-10-01 12:00:00', '9999-12-31 23:59:59', 'test', 'test'),
(2, 'B社', 2, '2020-10-01 12:00:00', '9999-12-31 23:59:59', 'test', 'test'),
(3, 'C社', 3, '2020-10-01 12:00:00', '9999-12-31 23:59:59', 'test', 'test');
