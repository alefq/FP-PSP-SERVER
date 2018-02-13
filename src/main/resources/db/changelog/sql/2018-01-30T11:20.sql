--Change columns: name, code, description, application_id, is_active to NOT NULL
ALTER TABLE ps_network.organizations ALTER COLUMN name SET NOT NULL;
ALTER TABLE ps_network.organizations ALTER COLUMN code SET NOT NULL;
ALTER TABLE ps_network.organizations ALTER COLUMN description SET NOT NULL;
ALTER TABLE ps_network.organizations ALTER COLUMN application_id SET NOT NULL;
ALTER TABLE ps_network.organizations ALTER COLUMN is_active SET NOT NULL;
