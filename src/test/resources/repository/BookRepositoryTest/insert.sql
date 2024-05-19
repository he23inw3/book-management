insert into tbl_genre (genre_id, genre_name, sort_order, valid_start_date, valid_end_date, created_by, updated_by)
values
(1, 'genreA', 1, '2020-10-01 12:00:00', '9999-12-31 23:59:59', 'init', 'init'),
(2, 'genreB', 2, '2020-10-01 12:00:00', '9999-12-31 23:59:59', 'init', 'init');

insert into tbl_publisher(publisher_id, publisher_name, sort_order, valid_start_date, valid_end_date, created_by, updated_by)
values
(1, 'publisherA', 1, '2000-10-01 12:00:00', '9999-12-31 23:59:59', 'init', 'init'),
(2, 'publisherB', 2, '2000-10-01 12:00:00', '9999-12-31 23:59:59', 'init', 'init');

insert into tbl_author(author_id, author_name, brief_history, valid_start_date, valid_end_date, created_by, updated_by) values
(1, 'authorA', 'commentA', '2000-10-01 12:00:00', '9999-12-31 23:59:59', 'init', 'init'),
(2, 'authorB', 'commentB', '2000-10-01 12:00:00', '9999-12-31 23:59:59', 'init', 'init');
