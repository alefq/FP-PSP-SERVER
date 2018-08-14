-- Add support for additional_properties in Person table
ALTER TABLE ps_families.person
  ADD COLUMN additional_properties jsonb NOT NULL DEFAULT '{}'::jsonb;