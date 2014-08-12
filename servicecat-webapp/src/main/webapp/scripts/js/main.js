(function() {
    var rdf = jassa.rdf;
    var vocab = jassa.vocab;
    var sparql = jassa.sparql;
    var service = jassa.service;
    var sponate = jassa.sponate;
    var facete = jassa.facete;
    var geo = jassa.geo;
    var util = jassa.util;
    
    var client = Jassa.client;

    var applyScope = function($scope) {
        if(!$scope.$$phase) {
            $scope.$apply();
        }
    };
    
    var storeApiUrl = 'api/services';

    var sparqlService = new jassa.service.SparqlServiceHttp('api/sparql', []);  
    var serviceConcept = facete.ConceptUtils.createTypeConcept('http://www.w3.org/ns/sparql-service-description#Service');

    
    var registerService = function(url) {
        var spec = {
            url: storeApiUrl + '/put',
            type: 'GET',
            data: {
                url: url
            },
            traditional: true,
            dataType: 'json'
        };

        var promise = jQuery.ajax(spec)
        .fail(function() {
            alert('Failed to retrieve dataset info');
        });
        
    };
    
    
    var labelMap = Jassa.sponate.SponateUtils.createDefaultLabelMap();

    var fetchDatasetDesc = function(uri) {
        var spec = {
            url: storeApiUrl + '/get',
            type: 'GET',
            data: {
                id: uri
            },
            traditional: true,
            dataType: 'json'
        };

        var promise = jQuery.ajax(spec)
        .fail(function() {
            alert('Failed to retrieve dataset info');
        });

        return promise;
    };

    
    angular.module('SparqlServiceCatalog', ['ui.router', 'ui.bootstrap', 'ui.jassa', 'ngGrid', 'ui.jassa.openlayers', 'ngSanitize'])

    .config([function() {
        // Setup drop down menu
        jQuery('.dropdown-toggle').dropdown();
       
        // Fix input element click problem
        jQuery('.dropdown input, .dropdown label').click(function(e) {
          e.stopPropagation();
        });        
    }])
    
    .config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise("/home");

        
        //
        // Now set up the states
        $stateProvider

        .state('home', {
            url: '/home',
            templateUrl: 'partials/home.html',
            controller: 'BrowseCtrl'
        })
        .state('home.list', {
            url: "/list",
            templateUrl: "partials/home.list.html",
            controller: function($scope) {
                //$scope.items = ["A", "List", "Of", "Items"];
            }
        })
        .state('register-service', {
            url: "/register-service",
            templateUrl: "partials/register-service.html",
            controller: 'RegisterServiceCtrl'
        })
        .state('faq', {
            url: "/faq",
            templateUrl: "partials/faq.html"
         })
        .state('about', {
            url: "/about",
            templateUrl: "partials/about.html"
        });
        
        
    }])

    .controller('BrowseCtrl', ['$scope', '$state', function($scope, $state) {

    }])
    
    .controller('RegisterServiceCtrl', ['$scope', '$state', function($scope, $state) {

        /**
         * Returns a function that wraps a function returning promises
         */
        var lastRequest = function(promiseFactory, cancelFn) {
            var deferred = null;
            var prior = null;
            var counter = 0;
            
            return function() {
                if(deferred == null) {
                    deferred = jQuery.Deferred();
                }

                //var args = arguments;

                var now = ++counter;
                //console.log('now ' + now + ' for ', args);
                var next = promiseFactory.apply(this, arguments);                
                
                if(cancelFn != null && prior != null) {
                    cancelFn(prior);
                }
                prior = next;
                
                next.then(function(data) {
                    if(now == counter) {
                        //console.log('resolved' + now + ' for ', args);
                        deferred.resolve.apply(this, data);
                        deferred = null;
                    }
                }).fail(function() {
                    if(now == counter) {
                        //console.log('rejected' + now + ' for ', args);
                        deferred.reject();
                        deferred = null;
                    }
                });
                
                
                return deferred.promise();
            };
            
        };
        
        
        
        var testSparql = function(serviceUrl) {
            var sparqlService = new service.SparqlServiceHttp(serviceUrl);
            var qe = sparqlService.createQueryExecution('Select * { <http://example.org/> <http://example.org/> ?o } Limit 1');
            var result = qe.execSelect();
            return result;
        };

        var lastCheck = lastRequest(testSparql);
        
        $scope.$watch('serviceUrl', function(serviceUrl) {
            //var p = testSparql(serviceUrl);
            var p = lastCheck(serviceUrl);

            p.then(function(rs) {
                $scope.isSparqlService = true;
            }).fail(function() {
                $scope.isSparqlService = false;
            }).always(function() {
                applyScope($scope);                
            });
        });
        
        $scope.submitForm = function(isValid) {

            var serviceUrl = $scope.serviceUrl;
            
            if (isValid) {
                registerService(serviceUrl);
            } else {
                alert('Fail');
            }
        };
    }])

    /*
    .controller('AppCtrl', ['$scope', '$q', '$parse', function($scope, $q, $parse) {

    }])
    */
    
    ;

})();
