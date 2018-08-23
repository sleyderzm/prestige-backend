INSERT INTO roles(id, name)
  SELECT 1, 'Admin'
  WHERE
    NOT EXISTS (
        SELECT id FROM roles WHERE id = 1
    );

INSERT INTO roles(id, name)
  SELECT 2, 'Client'
  WHERE
    NOT EXISTS (
        SELECT id FROM roles WHERE id = 2
    );

INSERT INTO roles(id, name)
  SELECT 3, 'Subscriber'
  WHERE
    NOT EXISTS (
        SELECT id FROM roles WHERE id = 3
    );


INSERT INTO clients (id, name)
  SELECT 1,'Prestige'
  WHERE
    NOT EXISTS (
        SELECT id FROM clients WHERE id = 1
    );
;

INSERT INTO users (id, first_name, last_name, email, password, client_id, role_id)
SELECT 1,'Admin','Prestige','admin@prestige.com','4cc799d17510e247a576f666883781b5534101b7c3b8dd8a7cf26ef9fc373e71',1,1
WHERE
  NOT EXISTS (
      SELECT id FROM users WHERE id = 1
  );
;

INSERT INTO projects (id, api_token, name, description, client_id)
  SELECT 1,'aec30666-41a9-48ad-8f03-18a78979ad75','Prestige KYC', 'This Project its for test', 1
  WHERE
    NOT EXISTS (
        SELECT id FROM projects WHERE id = 1
    );
;

INSERT INTO coins (id, api_id, name, symbol, value, updated_at)
  SELECT 1, '1027', 'Ethereum', 'ETH', 273.251529973, now()
  WHERE
    NOT EXISTS (
        SELECT id FROM coins WHERE id = 1
    );
;

INSERT INTO coins (id, api_id, name, symbol, value, updated_at)
  SELECT 2, '1', 'Bitcoin', 'BTC', 6403.96046684, now()
  WHERE
    NOT EXISTS (
        SELECT id FROM coins WHERE id = 2
    );
;

INSERT INTO coins (id, api_id, name, symbol, value, updated_at)
  SELECT 3, '1376', 'NEO', 'NEO', 17.1863530654, now()
  WHERE
    NOT EXISTS (
        SELECT id FROM coins WHERE id = 3
    );
;

INSERT INTO coins (id, api_id, name, symbol, value, updated_at)
  SELECT 4, null, 'Dollar', 'USD', 1, now()
  WHERE
    NOT EXISTS (
        SELECT id FROM coins WHERE id = 4
    );
;