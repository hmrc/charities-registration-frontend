# charities-registration-frontend

This microservice is an electronic version of the ChA1 form. This form can be found at : http://www.hmrc.gov.uk/charities/cha1.pdf

Registration will allow users to register
their Charity online with HMRC to be able to go on and claim gift aid, and Variation will allow users to amend their
registered details (agents, nominees, bank details) online.

### Dependencies

| Service                 | Link                                                |
|-------------------------|-----------------------------------------------------|
| address-lookup-frontend | https://github.com/hmrc/address-lookup-frontend.git |
| auth                    | https://github.com/hmrc/auth                        |
| charities               | https://github.com/hmrc/charities.git               |


## Running the Application

To run the application:

* `Clone the repository using SSH: git@github.com:hmrc/charities-registration-frontend.git` to local machine.
* `cd` to the root of the project.
* `sbt run`
* In your browser navigate to [http://localhost:9457/register-charity-hmrc/check-eligibility/register-the-charity](http://localhost:9457/register-charity-hmrc/check-eligibility/register-the-charity)
* only organisation users are allowed for gg login

### Prerequisites:
This service is written in Scala and the Play Framework, therefore you will need at least a Java Runtime Environment to run it. You will also need mongodb by either locally installing it or running a mongo docker container.

You should ensure that you have the latest version of the correct services and are running them through the service manager 2.

Make sure sm2 and service-manager-config are up to date

Then run the following command to start all services:

```bash
sm2 --start CHARITIES_REGISTRATION_ALL --appendArgs '{"CHARITIES_REGISTRATION_FRONTEND": ["-J-Dmicroservice.services.address-lookup-frontend.port=6001"]}'
```

**Ensure CHARITIES_REGISTRATION_FRONTEND uses port 6001 for address-lookup-frontend. Note: Update address-lookup-frontend port to 6001 in the application.conf of charities-registration-frontend**

## Tests

Below command will run unit tests, integration tests, formatting, coverage and check dependencies:

```bash
./run_all_tests.sh
```

### Journey tests and prototype

Please run the app with mocked address lookup for journey tests with script:

```bash
./run_for_journey_tests.sh
```

| Repositories  | Link                                                             |
|---------------|------------------------------------------------------------------|
| Journey tests | https://github.com/hmrc/charities-registration-journey-tests.git |
| Prototype     | https://charities-prototype-v1.herokuapp.com/admin/listings      |

## Accessibility Tests

### Prerequisites
Have node installed on your machine

### Execute tests
To run the tests locally, simply run:

```bash
sbt clean A11y/test
```
