# generated by datamodel-codegen:
#   filename:  schema/api/services/createStorageService.json
#   timestamp: 2021-11-09T23:02:30+00:00

from __future__ import annotations

from typing import Optional

from pydantic import BaseModel, Field, constr

from ...entity.services import storageService


class CreateStorageServiceEntityRequest(BaseModel):
    name: constr(min_length=1, max_length=64) = Field(
        ..., description='Name that identifies the this entity instance uniquely'
    )
    description: Optional[str] = Field(
        None, description='Description of Storage entity.'
    )
    serviceType: Optional[storageService.StorageServiceType] = None
