-- auxiliar scripts to drop tables
drop table comment_class;
drop table comment;
drop table processed_comment;

#1 create table in postgresql
drop table if exists comment_class;
create table comment_class (
    id serial primary key,
    projectName text,
    fileName text,
    className text,
    access text,
    isAbstract text,
    isEnum text,
    isInterface text, 
    startline integer,
    endline integer, 
    analyzed integer
); 

drop table if exists comment;
CREATE TABLE comment (
    commentClassId integer,
    startLine integer, 
    endLine integer,
    commentText text,
    commentType text,
    location text,
    description text , 
    dictionary_hit text, 
    jdeodorant_hit text, 
    refactoring_list_name text
);

drop table if exists processed_comment;
CREATE TABLE processed_comment (
    commentClassId integer,
    startLine integer, 
    endLine integer,
    commentText text,
    commentType text,
    location text,
    description text, 
    dictionary_hit text, 
    jdeodorant_hit text, 
    refactoring_list_name text
);

#2 run eclipse.commandline application adapted to run with postgresql to parse the comments and collect the raw comments.
-- alternatively , is possible to run sqlite_to_postgresql.py for each sqlite database file. i did not do that because 
-- the sqlite files as not reliable and there was multiple versions of them.

-- check both databases
select count(*) from comment_class where projectName = 'apache-ant-1.7.0';
select count(*) from comment_class where projectName = 'apache-jmeter-2.10';
select count(*) from comment_class where projectName like 'argouml%';
select count(*) from comment_class where projectName = 'columba-1.4-src';
select count(*) from comment_class where projectName = 'emf-2.4.1';
select count(*) from comment_class where projectName = 'hibernate-distribution-3.3.2.GA';
select count(*) from comment_class where projectName = 'jEdit-4.2';
select count(*) from comment_class where projectName = 'jfreechart-1.0.19';
select count(*) from comment_class where projectName = 'jruby-1.4.0';
select count(*) from comment_class where projectName = 'sql12';
-- in postgresql vs lite number of classes
  1475 1475 ok                
  1181 1181 ok            
  2610 2609 nok argo (need to be corrected , evernote created)       
  1711 1711 ok            
  1458 1458 ok            
  1356 1356 ok
   800 800  ok            
  1065 1065 ok
  1486 1486 ok            
  3108 3108 ok
select count(*) from comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'apache-ant-1.7.0';
select count(*) from comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'apache-jmeter-2.10';
select count(*) from comment a, comment_class b where a.commentclassid = b.id  and b.projectname like 'argouml%';
select count(*) from comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'columba-1.4-src';
select count(*) from comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'emf-2.4.1';
select count(*) from comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'hibernate-distribution-3.3.2.GA';
select count(*) from comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'jEdit-4.2';
select count(*) from comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'jfreechart-1.0.19';
select count(*) from comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'jruby-1.4.0';
select count(*) from comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'sql12';
-- in postgresql vs lite number of comments
 21587 21587 ok
 20084 20084 ok
 67719 67716 nok argo  (need to be corrected , evernote created)       
 33895 33895 ok
 25229 25229 ok
 11630 11630 ok
 16991 16991 ok
 23123 23123 ok
 11149 11149 ok
 27474 27474 ok

#3 dump the database in the raw state. keep this state of the database (available in dropbox)

#4 create index as necessary 
CREATE INDEX idx_comment_comment_class_id ON comment (commentclassid);
CREATE INDEX idx_processed_comment_comment_class_id ON processed_comment (commentclassid);

#5 dump database with the created indexes

#6 run filters using eclipse.commandline application
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'apache-ant-1.7.0';
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'apache-jmeter-2.10';
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname like 'argouml%';
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'columba-1.4-src';
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'emf-2.4.1';
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'hibernate-distribution-3.3.2.GA';
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'jEdit-4.2';
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'jfreechart-1.0.19';
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'jruby-1.4.0';
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'sql12';
-- in postgresql vs lite number of comments
--(third column is final after the correction of the inner class)
  4417 4436    4140
  7973 8126    8163
 10189 10303   9788 
  6675 6825    6569
  5085 5868    4401
  3032 3071    2968
 11205 11232  10322 
  4412 4449    4433
  5081 5176    4901
  8519 8627    7330



(the difference is normal because of the order of the filters that changed , see evernote log for more information)

#7 dump database with filtered comments

#8 insert classification columns into processed_comments
ALTER TABLE comment add column classification text;
ALTER TABLE processed_comment add column classification text;

#insert already classified comments into the database (ant, jmeter, freechart)

#9 Run web applicarition to classify all comments

-- get numbers 
-- everything that was classified
select count(*) from processed_comment where classification is not null;
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'apache-ant-1.7.0' and a.classification is not null;
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'apache-jmeter-2.10' and a.classification is not null;
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'jfreechart-1.0.19' and a.classification is not null;
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname like 'argouml%' and a.classification is not null;

  4140
  8163
  4433
  9788
total 26524

-- everything that was classified as without classification (bug fix comments are not a category of technical debt is that why it is here)
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'apache-ant-1.7.0' and a.classification in ('WITHOUT_CLASSIFICATION' ,'BUG_FIX_COMMENT');
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'apache-jmeter-2.10' and a.classification in ('WITHOUT_CLASSIFICATION' ,'BUG_FIX_COMMENT');
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'jfreechart-1.0.19' and a.classification in ('WITHOUT_CLASSIFICATION', 'BUG_FIX_COMMENT');
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname like 'argouml%' and a.classification in ('WITHOUT_CLASSIFICATION', 'BUG_FIX_COMMENT');

  4006
  7788
  4214
  8135 
total 24143  

-- everything that was clasified as technical debt
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'apache-ant-1.7.0' and a.classification not in ('WITHOUT_CLASSIFICATION' ,'BUG_FIX_COMMENT');
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'apache-jmeter-2.10' and a.classification not in ('WITHOUT_CLASSIFICATION' ,'BUG_FIX_COMMENT');
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'jfreechart-1.0.19' and a.classification not in ('WITHOUT_CLASSIFICATION', 'BUG_FIX_COMMENT');
select count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname like 'argouml%' and a.classification not in ('WITHOUT_CLASSIFICATION', 'BUG_FIX_COMMENT');
   134
   375
   219
  1653
total 2381

-- techinical distribution per project
select a.classification, count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'apache-ant-1.7.0' and a.classification not in ('WITHOUT_CLASSIFICATION' ,'BUG_FIX_COMMENT') group by 1; 
----------------+-------
 DESIGN         |    95
 TEST           |    10
 IMPLEMENTATION |    16
 DEFECT         |    13
select a.classification, count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'apache-jmeter-2.10' and a.classification not in ('WITHOUT_CLASSIFICATION' ,'BUG_FIX_COMMENT') group by 1;
----------------+-------
 DESIGN         |   316
 DOCUMENTATION  |     3
 IMPLEMENTATION |    22
 TEST           |    12
 DEFECT         |    22
select a.classification, count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname = 'jfreechart-1.0.19' and a.classification not in ('WITHOUT_CLASSIFICATION', 'BUG_FIX_COMMENT') group by 1;
---------------+-------
 DESIGN         |   184
 IMPLEMENTATION |    25
 TEST           |     1
 DEFECT         |     9
select a.classification, count(*) from processed_comment a, comment_class b where a.commentclassid = b.id  and b.projectname like 'argouml%' and a.classification not in ('WITHOUT_CLASSIFICATION', 'BUG_FIX_COMMENT') group by 1;
----------------+-------
 DESIGN         |   801
 DOCUMENTATION  |    30
 TEST           |    44
 IMPLEMENTATION |   651
 DEFECT         |   127




