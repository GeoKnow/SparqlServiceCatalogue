
CREATE TABLE IF NOT EXISTS "user" (
    "id" BIGINT PRIMARY KEY,
    "comment" text NOT NULL DEFAULT '' 
);


CREATE TABLE IF NOT EXISTS "dataset" (
    "id" BIGINT PRIMARY KEY,
    "name" text UNIQUE NOT NULL,
    "creation_date" DATE NOT NULL DEFAULT DATE(), -- Note: This is the date of the creation of the database record - NOT of the dataset!
    "display_name" text NOT NULL,
    "is_deleted" boolean NOT NULL DEFAULT false
); 

CREATE TABLE "sparql_location" (
    "id" INT BIGINT PRIMARY KEY,
    "dataset_id" BIGINT REFERENCES "dataset"("id"),
    "url" text NOT NULL,
    "fixed_score" INT -- Non-null fixed scores take precedence over any other scores (e.g. community scores) regardless of value    
);

CREATE TABLE "sparql_location_graph"(
    "id" INT BIGINT PRIMARY KEY,
    "sparql_location_id" REFERENCES "sparql_location"("id"),
    "url" text NOT NULL
);

/*
CREATE TABLE IF NOT EXISTS "relation_type" (
    "id" BIGINT PRIMARY KEY,
    "url" text NOT NULL
);
*/

CREATE TABLE IF NOT EXISTS "dataset_relation"(
    --"relation_type_id" REFERENCES "relation_type"("id"),
    "relation_type" text NOT NULL,
    "source_dataset" REFERENCES "dataset"("id"),
    "target_dataset" REFERENCES "dataset"("id"),
);

/**
 * A dataset may contain a set of other datasets.
 * If the containment is marked as complete.
 * All complete datasets having exactly the same set of contained datasets are considered equal.
 * 
 */
CREATE TABLE "dataset_containment"(
	"id" INT BIGINT PRIMARY KEY,
	"dataset_id" BIGINT REFERENCES "dataset"("id"),
	"is_complete" TINY INT NOT NULL DEFAULT false
);

CREATE TABLE "dataset_containment_set"(
	"dataset_containment_id" BIGINT REFERENCES "dataset_containment"("id")
	"dataset_id" REFERENCES "dataset"("id")
);

/**
 * A dataset may have 0 or more download locations.
 * A download location is assumed to point to an RDF file (possibly container, such as Zip)
 * In general the application has to cope with it.
 */
CREATE TABLE "dataset_download_location" (
    id INT PRIMARY KEY,
    dataset_id BIGINT REFERENCES "dataset"("id"),
    url text NOT NULL
);


