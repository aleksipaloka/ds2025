# ds2025
ds 2025 it2022081 it2022128

## Configuration

In order for the email service to work properly, a `.env.properties` file should be made, containing the following variables:

SENDGRID_API_KEY=your_api_key_here  
SENDGRID_FROM_EMAIL=verified_sender_email

This file is intentionally excluded from version control.

Otherwise, if those are not available, email.provider.enabled in application.properties should be turned false, for GreenRide to run.



