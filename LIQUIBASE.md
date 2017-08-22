# Introduction
Liquibase is a framework written in Java used to manage and apply your sql files. Their website sports the tagline “source control for your database”, which might be a bit misleading. Liquibase is not a version control system like Git or Subversion. In fact it is meant to be used in tandem with a version control system. When you use Liquibase you will have a project, just like any old Java project, that contains your sql files. When you run this project Liquibase will install your changes to the database. You can also embed Liquibase (and your sql files) into an existing project, allowing your application to manage its own database. Liquibase is meant to bring the management and deployment of your sql files into the familiar developer realms of IDE’s, version control, and continuous integration.

# Commands
## Create a database
The initial change log files for the database are already created under the name `db-changelog-master.xml` and `db-changelog-evolution.xml` in the `src/main/resources/db/changelog/` directory. So its not neccesary to generate this files again. All our database changes will take place in the `db-changelog-evolution.xml`.

If you are interested in know how to generate a change log file you can follow this [link](http://www.liquibase.org/documentation/command_line.html).

## Update and existing database
Changes are made using "change sets". Each changeSet tag must be uniquely identified by the combination of the “id” and "author" tag, otherwise Liquibase will skip the changes unless we explicitly tell him not to.

### Liquibase Formatted SQL

An easy way to add  a change set is to write the changes to the database as we normally do, using SQL, and adding a couple extra lines to make it a Liquibase-formatted SQL file. For example:

```
--liquibase formatted sql
--changeset codecentric-docs:release_1.create_tables.sql
 
CREATE TABLE SampleTable
(
  id uuid NOT NULL,
  someNumber INTEGER,
  CONSTRAINT sampletable_pkey PRIMARY KEY (id)
);
ALTER TABLE SampleTable
  OWNER TO sample;
 
--rollback DROP TABLE SampleTable;
```

The lines starting with "-" is what makes the file a Liquibase-formatted SQL-file. Only a few additional lines are needed.

```
--liquibase formatted sql
```

The above statement simply introduces the file as a Liquibase-formatted SQL-file.

```
--changeset codecentric-docs:release_1.create_tables.sql
```

As Liquibase is working on Changesets the name of the changeset introduced with this file must be defined. The part before the “:” is the author and the part after the “:” the name of the changeset itsself. We will use the following convention:

``
--changeset <author_username>:<yyyy-mm-ddThh:mm>.sql
``

The ID is based on the [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601).

One really nice feature is the possibility to define rollback statements. If used properly it is possible to return to an earlier state of the database this way. It should be noted that a Liquibase-formatted SQL-file can contain several SQL-statements and thus several rollback statements.

Now the only thing thats left is to include this file in the `db-changelog-evolution.xml` as follows:
```
<include file="<path_to_the_sql_file.sql" />
```

### Using Liquibase changeset format
Another way to introduce a change set is using Liquibase syntax. For example
```
<changeSet id="1" author="bob">
     <comment>A sample change log</comment>
     <createTable/>
</changeSet>

<changeSet id="2" author="bob" runAlways="true">
     <alterTable/>
</changeSet>
```

Where "id" and "author" must follow the same conventions previously discussed.

`runAlways="true"` executes the change set on every run, even if it has been run before.

To learn more about how to write change sets using the Liquibase format you can visit [this link](http://www.liquibase.org/documentation/changeset.html)






