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
            controller: 'SearchCtrl'
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

    .controller('SearchCtrl', ['$scope', function($scope) {

        // Create the SPARQL concept that identifies datasets    
        var concept = facete.ConceptUtils.createTypeConcept('http://www.w3.org/ns/sparql-service-description#Service');
        
        // Create a facetTreeConfig with the dataset config as its base
        var typeAheadFtc = new facete.FacetTreeConfig();
        typeAheadFtc.getFacetConfig().setBaseConcept(concept);
        
        // Init the sparql service (and wrap it with a cache and pagination)
        var sparqlService = new service.SparqlServiceHttp('http://localhost/data/servicecat/sparql', []);
        sparqlService = new service.SparqlServiceCache(sparqlService);
        sparqlService = new service.SparqlServicePaginate(sparqlService, 1000);

        // Set up the facet typeahead config (ftac)
        $scope.ftac = {
            sparqlService: sparqlService,
            facetTreeConfig: typeAheadFtc
        };

        // Create a variable to hold the dataset description
        $scope.service = {
            endpoint: '',
            graphName: ''
        };

    }])
    
    .controller('BrowseCtrl', ['$scope', '$state', function($scope, $state) {

    }])
    
    .controller('RegisterServiceCtrl', ['$scope', '$state', function($scope, $state) {

        /**
         * Wraps a factory that delays between requests
         */
        var postpone = function(promiseFactory, ms, abortFn) {
            return function() {
                var args = arguments;
                var deferred = jQuery.Deferred();
                var running = null;
                
                var timeout = setTimeout(function() {
                    running = promiseFactory.apply(this, args);                
                    running.then(function() {
                        deferred.resolve.apply(this, arguments);
                    }).fail(function() {
                        deferred.reject.apply(this, arguments);
                    });
                }, ms);
                
                var result = deferred.promise();
                result.abort = function() {
                    if(running == null) {
                        clearTimeout(timeout);
                    } else if(abortFn != null) {
                        abortFn(running);
                    }
                };
                
                return result;
            };
        };
        
        /**
         * Returns a function returing a promise that wraps a function returning promises.
         * Only the resolution of the most recently created promise will be resolved.
         */
        var lastRequest = function(promiseFactory, abortFn) {
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
                
                if(abortFn != null && prior != null) {
                    abortFn(prior);
                }
                prior = next;
                
                next.then(function() {
                    if(now == counter) {
                        //console.log('resolved' + now + ' for ', args);
                        deferred.resolve.apply(this, arguments);
                        deferred = null;
                    }
                }).fail(function() {
                    if(now == counter) {
                        //console.log('rejected' + now + ' for ', args);
                        deferred.reject.apply(this, arguments);
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

        var abortFn = function(p) { p.abort(); };
        var lastCheck = lastRequest(postpone(testSparql, 300), abortFn);
        
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
