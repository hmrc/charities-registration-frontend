## charities-registration-frontend

[ ![Download](https://api.bintray.com/packages/hmrc/releases/charities-registration-frontend/images/download.svg) ](https://bintray.com/hmrc/releases/tax-history-frontend/_latestVersion)

This microservice is an electronic version of the ChA1 form. This form can be found at : http://www.hmrc.gov.uk/charities/cha1.pdf

Registration will allow users to register
their Charity online with HMRC to be able to go on and claim gift aid, and Variation will allow users to amend their
registered details (agents, nominees, bank details) online.

### Dependencies

|Service                |Link                                                 |
|-----------------------|-----------------------------------------------------|
| address-lookup-frontend |https://github.com/hmrc/address-lookup-frontend.git|
| auth                    |https://github.com/hmrc/auth                       |
| charities               |https://github.com/hmrc/charities.git              |
| save4later              |https://github.com/hmrc/save4later.git             |

### Running the Application

To run the application:

* `Clone the repository using SSH: git@github.com:hmrc/charities-registration-frontend.git` to local machine.
* `cd` to the root of the project.
* `sbt run`
* In your browser navigate to [http://localhost:9457/register-charity-hmrc/check-eligibility/register-the-charity](http://localhost:9457/register-charity-hmrc/check-eligibility/register-the-charity)
* only organisation users are allowed for gg login

#### Prerequisites:
This service is written in Scala and the Play Framework, therefore you will need at least a Java Runtime Environment to run it. You will also need mongodb by either locally installing it or running a mongo docker container.

You should ensure that you have the latest version of the correct services and are running them through the service manager.

Make sure service-manager and service-manager-config are up to date

Then run the following command to start all services:

```
sm --start CHARITIES_REGISTRATION_ALL --appendArgs '{"ADDRESS_LOOKUP_FRONTEND":["-J-Dapplication.router=testOnlyDoNotUseInAppConf.Routes","-J-Dmicroservice.hosts.allowList.1=localhost"]}'
```

#### Tests:
Please run tests with any work changes
```
sbt test
```
below command will help to run unit tests, integration tests and coverage 
```
./check.sh
```
this can also be done with below command
```
sbt clean scalastyle coverage test coverageReport
```

### Journey tests and prototype

|Repositories     |Link                                                                   |
|-----------------|-----------------------------------------------------------------------|
|Journey tests    |https://github.com/hmrc/charities-registration-journey-tests.git       |
|Prototype        |https://charities-prototype-v1.herokuapp.com/admin/listings            |