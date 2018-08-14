-- Existing NULL values for user_id were purged on Testing and 
-- Demo (Production had none) 
ALTER TABLE data_collect.snapshots_economics
   ALTER COLUMN user_id SET NOT NULL;
