-- Geospatial System Migration
-- V10__geospatial_system.sql

-- Create geo_points table for tracking driver locations
CREATE TABLE geo_points (
    id BIGSERIAL PRIMARY KEY,
    driver_id BIGINT NOT NULL,
    delivery_id BIGINT NOT NULL,
    lat DOUBLE PRECISION NOT NULL,
    long DOUBLE PRECISION NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    speed_kmh DOUBLE PRECISION,
    heading_degrees DOUBLE PRECISION,
    accuracy_meters DOUBLE PRECISION,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create indexes for geo_points table
CREATE INDEX idx_geo_points_driver_id ON geo_points(driver_id);
CREATE INDEX idx_geo_points_delivery_id ON geo_points(delivery_id);
CREATE INDEX idx_geo_points_timestamp ON geo_points(timestamp);
CREATE INDEX idx_geo_points_active ON geo_points(active);
CREATE INDEX idx_geo_points_driver_timestamp ON geo_points(driver_id, timestamp);
CREATE INDEX idx_geo_points_delivery_timestamp ON geo_points(delivery_id, timestamp);
CREATE INDEX idx_geo_points_driver_delivery ON geo_points(driver_id, delivery_id);
CREATE INDEX idx_geo_points_location ON geo_points(lat, long);

-- Create spatial index for location-based queries (PostGIS extension required)
-- CREATE INDEX idx_geo_points_spatial ON geo_points USING GIST (ST_SetSRID(ST_MakePoint(long, lat), 4326));

-- Create composite indexes for common query patterns
CREATE INDEX idx_geo_points_driver_active_timestamp ON geo_points(driver_id, active, timestamp DESC);
CREATE INDEX idx_geo_points_delivery_active_timestamp ON geo_points(delivery_id, active, timestamp DESC);

-- Create function to calculate distance between two points (Haversine formula)
CREATE OR REPLACE FUNCTION calculate_distance_km(
    lat1 DOUBLE PRECISION,
    lon1 DOUBLE PRECISION,
    lat2 DOUBLE PRECISION,
    lon2 DOUBLE PRECISION
) RETURNS DOUBLE PRECISION AS $$
DECLARE
    R DOUBLE PRECISION := 6371; -- Earth's radius in kilometers
    dlat DOUBLE PRECISION;
    dlon DOUBLE PRECISION;
    a DOUBLE PRECISION;
    c DOUBLE PRECISION;
BEGIN
    -- Convert to radians
    dlat := radians(lat2 - lat1);
    dlon := radians(lon2 - lon1);
    lat1 := radians(lat1);
    lat2 := radians(lat2);
    
    -- Haversine formula
    a := sin(dlat/2) * sin(dlat/2) + cos(lat1) * cos(lat2) * sin(dlon/2) * sin(dlon/2);
    c := 2 * atan2(sqrt(a), sqrt(1-a));
    
    RETURN R * c;
END;
$$ LANGUAGE plpgsql;

-- Create function to find drivers within radius
CREATE OR REPLACE FUNCTION find_drivers_within_radius(
    center_lat DOUBLE PRECISION,
    center_lon DOUBLE PRECISION,
    radius_km DOUBLE PRECISION
) RETURNS TABLE(
    driver_id BIGINT,
    lat DOUBLE PRECISION,
    long DOUBLE PRECISION,
    distance_km DOUBLE PRECISION
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        gp.driver_id,
        gp.lat,
        gp.long,
        calculate_distance_km(center_lat, center_lon, gp.lat, gp.long) as distance_km
    FROM geo_points gp
    WHERE gp.id IN (
        SELECT MAX(gp2.id) 
        FROM geo_points gp2 
        WHERE gp2.active = true 
        GROUP BY gp2.driver_id
    )
    AND calculate_distance_km(center_lat, center_lon, gp.lat, gp.long) <= radius_km
    ORDER BY distance_km;
END;
$$ LANGUAGE plpgsql;

-- Create function to get latest location for each driver
CREATE OR REPLACE FUNCTION get_latest_driver_locations() 
RETURNS TABLE(
    driver_id BIGINT,
    delivery_id BIGINT,
    lat DOUBLE PRECISION,
    long DOUBLE PRECISION,
    timestamp TIMESTAMP,
    speed_kmh DOUBLE PRECISION,
    heading_degrees DOUBLE PRECISION
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        gp.driver_id,
        gp.delivery_id,
        gp.lat,
        gp.long,
        gp.timestamp,
        gp.speed_kmh,
        gp.heading_degrees
    FROM geo_points gp
    WHERE gp.id IN (
        SELECT MAX(gp2.id) 
        FROM geo_points gp2 
        WHERE gp2.active = true 
        GROUP BY gp2.driver_id
    );
END;
$$ LANGUAGE plpgsql;

-- Create function to get delivery route points
CREATE OR REPLACE FUNCTION get_delivery_route(
    delivery_id_param BIGINT
) RETURNS TABLE(
    driver_id BIGINT,
    lat DOUBLE PRECISION,
    long DOUBLE PRECISION,
    timestamp TIMESTAMP,
    speed_kmh DOUBLE PRECISION
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        gp.driver_id,
        gp.lat,
        gp.long,
        gp.timestamp,
        gp.speed_kmh
    FROM geo_points gp
    WHERE gp.delivery_id = delivery_id_param
    AND gp.active = true
    ORDER BY gp.timestamp ASC;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_geo_points_updated_at 
    BEFORE UPDATE ON geo_points 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Create view for active drivers summary
CREATE VIEW active_drivers_summary AS
SELECT 
    COUNT(DISTINCT driver_id) as total_active_drivers,
    COUNT(*) as total_location_updates,
    AVG(speed_kmh) as average_speed_kmh,
    MAX(timestamp) as last_update_time
FROM geo_points 
WHERE active = true 
AND timestamp >= CURRENT_TIMESTAMP - INTERVAL '1 hour';

-- Create view for delivery route statistics
CREATE VIEW delivery_route_statistics AS
SELECT 
    delivery_id,
    driver_id,
    COUNT(*) as route_points,
    MIN(timestamp) as start_time,
    MAX(timestamp) as end_time,
    AVG(speed_kmh) as average_speed_kmh,
    calculate_distance_km(
        MIN(lat), MIN(long), 
        MAX(lat), MAX(long)
    ) as total_distance_km
FROM geo_points 
WHERE active = true
GROUP BY delivery_id, driver_id;

-- Insert sample geo points for testing
INSERT INTO geo_points (driver_id, delivery_id, lat, long, timestamp, speed_kmh, heading_degrees, accuracy_meters) VALUES
-- Driver 1 locations (Addis Ababa area)
(1, 1, 9.1450, 40.4897, CURRENT_TIMESTAMP - INTERVAL '30 minutes', 25.0, 45.0, 10.0),
(1, 1, 9.1500, 40.4950, CURRENT_TIMESTAMP - INTERVAL '20 minutes', 30.0, 50.0, 8.0),
(1, 1, 9.1550, 40.4997, CURRENT_TIMESTAMP - INTERVAL '10 minutes', 35.0, 55.0, 5.0),

-- Driver 2 locations (different area)
(2, 2, 9.1600, 40.5100, CURRENT_TIMESTAMP - INTERVAL '25 minutes', 20.0, 30.0, 12.0),
(2, 2, 9.1650, 40.5150, CURRENT_TIMESTAMP - INTERVAL '15 minutes', 28.0, 35.0, 9.0),
(2, 2, 9.1700, 40.5200, CURRENT_TIMESTAMP - INTERVAL '5 minutes', 32.0, 40.0, 7.0),

-- Driver 3 locations (near Driver 1 for clustering test)
(3, 3, 9.1560, 40.5000, CURRENT_TIMESTAMP - INTERVAL '12 minutes', 22.0, 48.0, 11.0),
(3, 3, 9.1570, 40.5010, CURRENT_TIMESTAMP - INTERVAL '6 minutes', 26.0, 52.0, 8.0),
(3, 3, 9.1580, 40.5020, CURRENT_TIMESTAMP - INTERVAL '2 minutes', 29.0, 54.0, 6.0);

-- Create index for performance on timestamp-based queries
CREATE INDEX idx_geo_points_timestamp_desc ON geo_points(timestamp DESC);

-- Create partial index for active records only
CREATE INDEX idx_geo_points_active_timestamp ON geo_points(timestamp) WHERE active = true;

-- Add comments for documentation
COMMENT ON TABLE geo_points IS 'Stores GPS location data for drivers during deliveries';
COMMENT ON COLUMN geo_points.driver_id IS 'ID of the driver';
COMMENT ON COLUMN geo_points.delivery_id IS 'ID of the delivery being tracked';
COMMENT ON COLUMN geo_points.lat IS 'Latitude coordinate';
COMMENT ON COLUMN geo_points.long IS 'Longitude coordinate';
COMMENT ON COLUMN geo_points.timestamp IS 'When this location was recorded';
COMMENT ON COLUMN geo_points.speed_kmh IS 'Speed in kilometers per hour';
COMMENT ON COLUMN geo_points.heading_degrees IS 'Direction in degrees (0-360)';
COMMENT ON COLUMN geo_points.accuracy_meters IS 'GPS accuracy in meters';

COMMENT ON FUNCTION calculate_distance_km IS 'Calculates distance between two GPS coordinates using Haversine formula';
COMMENT ON FUNCTION find_drivers_within_radius IS 'Finds all drivers within a specified radius of a center point';
COMMENT ON FUNCTION get_latest_driver_locations IS 'Gets the most recent location for each active driver';
COMMENT ON FUNCTION get_delivery_route IS 'Gets all location points for a specific delivery route';
