(ns latakia.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [latakia.core-test]
   [latakia.common-test]))

(enable-console-print!)

(doo-tests 'latakia.core-test
           'latakia.common-test)
