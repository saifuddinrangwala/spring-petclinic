@Library('my-pipeline-library') _

myDeliveryPipeline(
  properties ([
        parameters([
                string(name: 'BRANCH', defaultValue: 'master', description: 'Branch to build'),
	        choice(name: 'RUN_TEST', choices: ['yes', 'no'], description: 'Run test while build'),
	        booleanParam(name: 'MAIL_TRIGGER', defaultValue: true, description: 'mail to be triggred'),
                string(name: 'EMAIL', defaultValue: 'mrcool435@gmail.com', description: 'Mail to be notified')
   
        ])
   ])
)
