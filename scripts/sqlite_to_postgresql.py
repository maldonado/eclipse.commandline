import sqlite3 as lite
import psycopg2
import sys


lite_con = None
psql_con = None

lite_table  = sys.argv[1]
# psql_pswd  = sys.argv[2]

try:
    # lite connection
    lite_con = lite.connect('/Users/evermal/Dropbox/all_design_debt_files/classification_database/db/classified_td.db')
    lite_cursor = lite_con.cursor()
    
    # pssql connection
    psql_con = psycopg2.connect(host='localhost', port='5432', database='comment_classification', user='evermal', password='evermalton')
    # psql_con = psycopg2.connect(host='localhost', port='5432', database='comment_classification', user='evermal', password=psql_pswd)
    psql_cursor = psql_con.cursor()

    lite_cursor.execute("select classification, comment_text from "+ lite_table + " where classification is not 'WITHOUT_CLASSIFICATION'")
    result = lite_cursor.fetchall()

    count = 0
    max_count =  len(result)
    for line in result:
        count = count + 1

        # comment_class from sqlite
        classification = line[0]
        comment_text = line[1].replace('\'', '"')

        # print classification
        # print comment_text

        # update processed comment
        psql_cursor.execute("update processed_comment set classification = '"+classification+"' where commenttext like '%"+comment_text+"%' ")
        
        print str(count)+" out of: "+str(max_count)

except Exception, e:
    print e
    psql_con.rollback()
finally:
    psql_con.commit()