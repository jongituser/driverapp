-- Notification System Migration
-- V9__notification_system.sql

-- Create notification type enum
CREATE TYPE notification_type AS ENUM ('SMS', 'EMAIL', 'PUSH');

-- Create notification language enum
CREATE TYPE notification_language AS ENUM ('AMHARIC', 'OROMO', 'TIGRINYA', 'ENGLISH');

-- Create notification status enum
CREATE TYPE notification_status AS ENUM ('PENDING', 'SENT', 'FAILED');

-- Create notification_templates table
CREATE TABLE notification_templates (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    type notification_type NOT NULL,
    language notification_language NOT NULL,
    subject VARCHAR(500),
    body TEXT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create notifications table
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    type notification_type NOT NULL,
    language notification_language NOT NULL,
    template_id BIGINT NOT NULL,
    status notification_status NOT NULL DEFAULT 'PENDING',
    sent_at TIMESTAMP,
    error_message TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create indexes for notification_templates
CREATE INDEX idx_notification_templates_code ON notification_templates(code);
CREATE INDEX idx_notification_templates_type ON notification_templates(type);
CREATE INDEX idx_notification_templates_language ON notification_templates(language);
CREATE INDEX idx_notification_templates_active ON notification_templates(active);
CREATE INDEX idx_notification_templates_code_language ON notification_templates(code, language);
CREATE INDEX idx_notification_templates_type_language ON notification_templates(type, language);

-- Create indexes for notifications
CREATE INDEX idx_notifications_recipient_id ON notifications(recipient_id);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_language ON notifications(language);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_template_id ON notifications(template_id);
CREATE INDEX idx_notifications_active ON notifications(active);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_recipient_status ON notifications(recipient_id, status);
CREATE INDEX idx_notifications_recipient_type ON notifications(recipient_id, type);
CREATE INDEX idx_notifications_recipient_language ON notifications(recipient_id, language);

-- Add foreign key constraints
ALTER TABLE notifications 
ADD CONSTRAINT fk_notifications_template_id 
FOREIGN KEY (template_id) REFERENCES notification_templates(id);

-- Create triggers for updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_notification_templates_updated_at 
    BEFORE UPDATE ON notification_templates 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_notifications_updated_at 
    BEFORE UPDATE ON notifications 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert sample notification templates
INSERT INTO notification_templates (code, type, language, subject, body) VALUES
-- Delivery assigned notifications
('DELIVERY_ASSIGNED', 'SMS', 'ENGLISH', NULL, 'Hello {name}, your delivery {deliveryId} has been assigned. ETA: {eta}'),
('DELIVERY_ASSIGNED', 'SMS', 'AMHARIC', NULL, 'ሰላም {name}፣ የድርጊትዎ {deliveryId} ተመድቧል። የሚደርስበት ጊዜ: {eta}'),
('DELIVERY_ASSIGNED', 'SMS', 'OROMO', NULL, 'Akkam {name}, dhiyeessii keessan {deliveryId} kan argamuudha. Yeroo dhiyeessii: {eta}'),
('DELIVERY_ASSIGNED', 'SMS', 'TIGRINYA', NULL, 'ሰላም {name}፣ ናብ ድርጊትኩም {deliveryId} ተመዲቡ። ግዜ መምጣት: {eta}'),

-- Email versions
('DELIVERY_ASSIGNED', 'EMAIL', 'ENGLISH', 'Delivery Assignment - {deliveryId}', 'Dear {name},<br><br>Your delivery {deliveryId} has been assigned to a driver. Expected delivery time: {eta}<br><br>Thank you for choosing our service.'),
('DELIVERY_ASSIGNED', 'EMAIL', 'AMHARIC', 'ድርጊት መመደብ - {deliveryId}', 'ውድ {name}፣<br><br>የድርጊትዎ {deliveryId} ለጥፈታ ተመድቧል። የሚደርስበት ጊዜ: {eta}<br><br>አገልግሎታችንን ስለረገጡ እናመሰግናለን።'),
('DELIVERY_ASSIGNED', 'EMAIL', 'OROMO', 'Dhiyeessii Argamu - {deliveryId}', 'Jaal {name},<br><br>Dhiyeessii keessan {deliveryId} kan konkolaataa argamuudha. Yeroo dhiyeessii filatamaa: {eta}<br><br>Nu gargaarsuuf galatoomi.'),
('DELIVERY_ASSIGNED', 'EMAIL', 'TIGRINYA', 'መመደብ ድርጊት - {deliveryId}', 'ውድ {name}፣<br><br>ድርጊትኩም {deliveryId} ናብ ጥፈታ ተመዲቡ። ግዜ መምጣት: {eta}<br><br>አገልግሎትና ስለረገጡ እናመስግን።'),

-- Push notification versions
('DELIVERY_ASSIGNED', 'PUSH', 'ENGLISH', 'Delivery Assigned', 'Your delivery {deliveryId} has been assigned. ETA: {eta}'),
('DELIVERY_ASSIGNED', 'PUSH', 'AMHARIC', 'ድርጊት ተመድቧል', 'የድርጊትዎ {deliveryId} ተመድቧል። የሚደርስበት ጊዜ: {eta}'),
('DELIVERY_ASSIGNED', 'PUSH', 'OROMO', 'Dhiyeessii Argamu', 'Dhiyeessii keessan {deliveryId} kan argamuudha. Yeroo dhiyeessii: {eta}'),
('DELIVERY_ASSIGNED', 'PUSH', 'TIGRINYA', 'ድርጊት ተመዲቡ', 'ድርጊትኩም {deliveryId} ተመዲቡ። ግዜ መምጣት: {eta}'),

-- Payment confirmation notifications
('PAYMENT_CONFIRMED', 'SMS', 'ENGLISH', NULL, 'Payment confirmed for delivery {deliveryId}. Amount: {amount} ETB. Thank you!'),
('PAYMENT_CONFIRMED', 'SMS', 'AMHARIC', NULL, 'የድርጊት {deliveryId} ክፍያ ተረጋግጧል። መጠን: {amount} ብር። እናመሰግናለን!'),
('PAYMENT_CONFIRMED', 'SMS', 'OROMO', NULL, 'Kaffaltii dhiyeessii {deliveryId} kan mirkaneessuudha. Baay''ii: {amount} ETB. Galatoomi!'),
('PAYMENT_CONFIRMED', 'SMS', 'TIGRINYA', NULL, 'ክፍያ ድርጊት {deliveryId} ተረጋጊጹ። መጠን: {amount} ብር። እናመስግን!'),

-- Delivery completed notifications
('DELIVERY_COMPLETED', 'SMS', 'ENGLISH', NULL, 'Your delivery {deliveryId} has been completed successfully. Thank you for using our service!'),
('DELIVERY_COMPLETED', 'SMS', 'AMHARIC', NULL, 'የድርጊትዎ {deliveryId} በተሳካት ተጠናቅቋል። አገልግሎታችንን ስለተጠቀሙ እናመሰግናለን!'),
('DELIVERY_COMPLETED', 'SMS', 'OROMO', NULL, 'Dhiyeessii keessan {deliveryId} kan guutamuu danda''uudha. Nu gargaarsuuf galatoomi!'),
('DELIVERY_COMPLETED', 'SMS', 'TIGRINYA', NULL, 'ድርጊትኩም {deliveryId} ብተሳካ ተዛዘም። አገልግሎትና ስለተጠቀሙ እናመስግን!'),

-- Driver earnings notifications
('DRIVER_EARNING', 'SMS', 'ENGLISH', NULL, 'You have earned {amount} ETB for delivery {deliveryId}. Total earnings: {totalEarnings} ETB'),
('DRIVER_EARNING', 'SMS', 'AMHARIC', NULL, 'ለድርጊት {deliveryId} {amount} ብር ተሰርዘዋል። አጠቃላይ ገቢ: {totalEarnings} ብር'),
('DRIVER_EARNING', 'SMS', 'OROMO', NULL, 'Dhiyeessii {deliveryId} argachuuf {amount} ETB argattan. Guutuu argachuu: {totalEarnings} ETB'),
('DRIVER_EARNING', 'SMS', 'TIGRINYA', NULL, 'ናብ ድርጊት {deliveryId} {amount} ብር ተሰርዘኹም። አጠቃላይ ገቢ: {totalEarnings} ብር'),

-- Welcome notifications
('WELCOME', 'EMAIL', 'ENGLISH', 'Welcome to Ethiopian Delivery Platform', 'Welcome {name}!<br><br>Thank you for joining our Ethiopian Delivery Platform. We are excited to serve you with the best delivery experience.<br><br>Best regards,<br>The Ethiopian Delivery Team'),
('WELCOME', 'EMAIL', 'AMHARIC', 'የኢትዮጵያ ድርጊት መድረኳ እንኳን በደህና መጡ', 'እንኳን በደህና መጡ {name}!<br><br>የኢትዮጵያ ድርጊት መድረኳችን ስለተቀላቀሉ እናመሰግናለን። ምርጥ የድርጊት ስሜት ለመስጠት ተደስተናል።<br><br>ከሰላምታ፣<br>የኢትዮጵያ ድርጊት ቡድን'),
('WELCOME', 'EMAIL', 'OROMO', 'Baga Ethiopian Delivery Platform gahani', 'Baga gahani {name}!<br><br>Nu gargaarsuuf galatoomi. Dhiyeessii sirrii ta''e argachuuf nu gargaaruu dandeessuudha.<br><br>Galatoomi,<br>Ethiopian Delivery Team'),
('WELCOME', 'EMAIL', 'TIGRINYA', 'ናብ ኢትዮጵያዊ ድርጊት መድረኽ እንኳን በደህና መጻእኩም', 'እንኳን በደህና መጻእኩም {name}!<br><br>ናብ ኢትዮጵያዊ ድርጊት መድረኽና ስለተተሓሓዝኩም እናመስግን። ምርጥ ድርጊት ክንህብ ተደስተናል።<br><br>ሰላምታ፣<br>ኢትዮጵያዊ ድርጊት ቡድን');

-- Create a view for notification statistics
CREATE VIEW notification_statistics AS
SELECT 
    nt.language,
    nt.type,
    COUNT(*) as template_count,
    COUNT(CASE WHEN nt.active = true THEN 1 END) as active_templates
FROM notification_templates nt
GROUP BY nt.language, nt.type;

-- Create a view for notification delivery statistics
CREATE VIEW notification_delivery_statistics AS
SELECT 
    n.language,
    n.type,
    n.status,
    COUNT(*) as notification_count,
    COUNT(CASE WHEN n.status = 'SENT' THEN 1 END) as sent_count,
    COUNT(CASE WHEN n.status = 'FAILED' THEN 1 END) as failed_count,
    COUNT(CASE WHEN n.status = 'PENDING' THEN 1 END) as pending_count
FROM notifications n
GROUP BY n.language, n.type, n.status;
