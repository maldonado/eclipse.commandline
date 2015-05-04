import sqlite3 as lite
import psycopg2
import sys


lite_con = None
psql_con = None

lite_db  = sys.argv[1]
# psql_pswd  = sys.argv[2]

try:
    # lite connection
    lite_con = lite.connect('/Users/evermal/Dropbox/all_design_debt_files/separeted_databases_per_project/'+lite_db)
    lite_cursor = lite_con.cursor()
    
    # pssql connection
    psql_con = psycopg2.connect(host='localhost', port='5432', database='comment_classification', user='evermal', password='evermalton')
    # psql_con = psycopg2.connect(host='localhost', port='5432', database='comment_classification', user='evermal', password=psql_pswd)
    psql_cursor = psql_con.cursor()

    lite_cursor.execute('select * from comment_class order by id')
    result = lite_cursor.fetchall()

    count = 0
    max_count =  len(result)
    for line in result:
        count = count + 1

        # comment_class from sqlite
        sqlite_comment_class_id = line[0]
        projectName = line[1]
        fileName = line[2]
        className = line[3]
        access = line[4]
        isAbstract = line[5]
        isEnum = line[6]
        isInterface = line[7]
        startline = line[8]
        endline = line[9]
        analyzed = line[10]

        # insert comment class
        psql_cursor.execute("insert into comment_class(projectName, fileName, className, access, isAbstract, isEnum, isInterface, startline, endline, analyzed ) values ('"+projectName+"', '"+fileName+"', '"+className+"', '"+access+"', '"+isAbstract+"', '"+isEnum+"', '"+isInterface+"', '"+str(startline)+"', '"+str(endline)+"', '"+str(analyzed)+"')")
        psql_cursor.execute("select last_value from comment_class_id_seq")
        psql_comment_class_id = psql_cursor.fetchone()[0]

        lite_cursor.execute("select * from comment where commentclassid = '"+str(sqlite_comment_class_id)+"' order by startline")
        comment_result = lite_cursor.fetchall()

        #insert comments for the class
        for comment_line in comment_result:

            # comment from sqlite
            commentClassId = psql_comment_class_id
            startLine = comment_line[2]
            endLine = comment_line[3]
            commentText = comment_line[4].replace('\'', '"')
            commentType = str(comment_line[5]).replace('\'', '"')
            location = comment_line[6]
            description = comment_line[7]
            dictionary_hit = comment_line[8]
            jdeodorant_hit = comment_line[9]
            refactoring_list_name = comment_line[10]

            psql_cursor.execute("insert into comment (commentclassid, startline, endline, commentText, commentType, location, description, dictionary_hit, jdeodorant_hit, refactoring_list_name) values ('"+str(commentClassId)+"','"+str(startLine)+"','"+str(endline)+"','"+commentText+"','"+commentType+"','"+str(location)+"','"+str(description)+"','"+str(dictionary_hit)+"','"+str(jdeodorant_hit)+"','"+str(refactoring_list_name)+"')")

        print str(count)+" out of: "+str(max_count)

except Exception, e:
    print e
    psql_con.rollback()
finally:
    psql_con.commit()