# generated by datamodel-codegen:
#   filename:  schema/entity/services/databaseService.json
#   timestamp: 2021-09-01T06:44:13+00:00

from __future__ import annotations

from enum import Enum
from typing import Optional

from pydantic import BaseModel, Field, constr

from ...type import basic, jdbcConnection, schedule


class DatabaseServiceType(Enum):
    BigQuery = 'BigQuery'
    MySQL = 'MySQL'
    Redshift = 'Redshift'
    Snowflake = 'Snowflake'
    Postgres = 'Postgres'
    MSSQL = 'MSSQL'
    Hive = 'Hive'
    Oracle = 'Oracle'
    Athena = 'Athena'
    Presto = 'Presto'


class DatabaseService(BaseModel):
    id: basic.Uuid = Field(
        ..., description='Unique identifier of this database service instance.'
    )
    name: constr(min_length=1, max_length=64) = Field(
        ..., description='Name that identifies this database service.'
    )
    serviceType: DatabaseServiceType = Field(
        ...,
        description='Type of database service such as MySQL, BigQuery, Snowflake, Redshift, Postgres...',
    )
    description: Optional[str] = Field(
        None, description='Description of a database service instance.'
    )
    href: basic.Href = Field(
        ..., description='Link to the resource corresponding to this database service.'
    )
    jdbc: jdbcConnection.JdbcInfo = Field(
        ..., description='JDBC connection information.'
    )
    ingestionSchedule: Optional[schedule.Schedule] = Field(
        None, description='Schedule for running metadata ingestion jobs.'
    )
