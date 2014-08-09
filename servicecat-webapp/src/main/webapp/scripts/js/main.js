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

        /*
        $scope.dataset = {
                desc: createEmptyDatasetSpec()
            };
        
        
        $scope.previewDataset = function(uri) {
            fetchDatasetDesc(uri).done(function(desc) {
                $scope.dataset.desc = desc;
                $state.go('home.list');
            }).then(function() {
                applyScope($scope);
            });
        };

        //sparqlService = new jassa.service.SparqlServiceCache(sparqlServiceCore);

        var prefixes = {
            'o': 'http://dcat.cc/ontology/',
            'r': 'http://dcat.cc/resource/',
            'rdf': 'http://www.w3.org/1999/02/22-rdf-syntax-ns#',
            'rdfs': 'http://www.w3.org/2000/01/rdf-schema#',
            'owl': 'http://www.w3.org/2002/07/owl#',
            'dcat': 'http://www.w3.org/ns/dcat#'
        };


        
        var store = new sponate.StoreFacade(sparqlService, prefixes);
        
        store.addMap(labelMap, 'labels');                
        
        $scope.search = {text: ''};
        
        $scope.$watch('search.text', function(val) {
            $scope.refreshResults(val); 
        });
  
        $scope.dataset = {
            desc: createEmptyDatasetSpec()
        };

        $scope.editMode = false;
        
        
        $scope.requestId = 1;
        
        $scope.refreshResults = function(re) {
            var requestId = ++$scope.requestId;

            if(!re) {
                re = '';
            }
            
            /*
            if(!re || re === '') {
                $scope.items = [];
                return;
            }
            * /
            
            var maxResults = 10;
            
            //var re = 'DB';
            //var concept =
            var criteria = {$or: [{id: {$regex: re}}, {hiddenLabels: {$elemMatch: {id: {$regex: re}}}}]};
            
            //var datasetConcept = sparql.ConceptUtils.createFromString('?s a <http://www.w3.org/ns/dcat#Dataset>', 's'); //new facete.Concept(sparql.ElementUtils.parse('?s a <http://www.w3.org/ns/dcat#Dataset>'), 's');
            
            
            
            
            
            
            var baseFlow =  store.labels.find(criteria).concept(datasetConcept);
            var countPromise = baseFlow.count();        
            
            // An empty search string yields no results, however the count is fetched
            var dataPromise;
            if(re === '') {
                dataPromise = jQuery.Deferred();
                dataPromise.resolve([]);
            } else {
                dataPromise = baseFlow.limit(maxResults).asList(true);
            }
            
            $.when.apply(window, [countPromise, dataPromise]).done(function(count, data) {
                if(requestId != $scope.requestId) {
                    return;
                }
                
                $scope.items = data;
                $scope.totalItemCount = count;
                console.log(data);
                applyScope($scope);                        
            });
            //promise.done(function(data) {
            //});
            
        };
        
        $scope.refreshResults();     
        */   
    }])
    
    .controller('RegisterServiceCtrl', ['$scope', '$state', function($scope, $state) {
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
