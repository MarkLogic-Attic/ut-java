var app = angular.module('app', []);
var page = 1;

// Can set to empty string (or other relative URL) if API is provided as part of webapp
var baseURI = "http://localhost:8080/exercise-1/";

app.controller('queryCtrl', function($scope, $http) {
	
	$scope.results = [];
	$scope.selected = {};
	$scope.selectedCount = 0;
	$scope.message = undefined;

	$scope.search = function() {
		page = 1;
		$scope.results = [];
		$scope.isSearching = true;
		$http.get(baseURI + 'search?q=' + encodeURIComponent($('#query').val()) + '&p=' + page).success(
			function(data) {
				$scope.isSearching = false;
				$scope.results = data.results;
				$scope.total = data.totalResults;
				$scope.facets = data.facets;
				$scope.selected = {};
				$scope.selectedCount = 0;
			}).error(function() {
				$scope.isSearching = false;
			});
		page = page + 1;
	};

	$scope.scroll = function() {
		if ($scope.isScrolling)
			return;
		
		$scope.isScrolling = true;
		$http.get(baseURI + 'search?q=' + encodeURIComponent($('#query').val()) + '&p=' + page).success(
			function(data) {
				$scope.isScrolling = false;
				for ( var i = 0; i < data.results.length; i++) {
					$scope.results.push(data.results[i]);
				}
				if (data.results.length > 0) {
					page = page + 1;
				}
			}).error(function() {
				$scope.isScrolling = false;
			});
		
	};

	$scope.updateSelection = function($event, uri) {
		var checkbox = $event.target;

		if (checkbox.checked) {
			$scope.selected[uri] = 1;
			$scope.selectedCount += 1;
		} else {
			delete $scope.selected[uri];
			$scope.selectedCount -= 1;
		}

	};

	$scope.tag = function() {
		if ($scope.selectedCount <= 0)
			return;

		$scope.isTagging = true;
		$http.post(baseURI + 'tag?t=' + encodeURIComponent($('#tag-text').val()),
			Object.keys($scope.selected)).success(function(data) {
			$scope.isTagging = false;
			$scope.tags = data.count;
			$scope.search();
		}).error(function() {
			$scope.isTagging = false;
			alert("Tagging failed!");
		});
		
	};

	$scope.showMessage = function(uri) {
		$http.get(baseURI + 'message' + uri).success(function(data) {
			$scope.message = data;
		}).error(function() {
			alert("Can't fetch " + uri);
		});
	};

	$scope.unimplemented = function() {
		alert("Unimplemented!!");
	};

});

app.directive('onEnter', function() {
	return function(scope, element, attrs) {
		element.bind("keydown keypress", function(event) {
			if (event.which === 13) {
				scope.$apply(function() {
					scope.$eval(attrs.onEnter);
				});
				event.preventDefault();
			}
		});
	};
});

app.directive('whenScrolled', function() {
	return function(scope, elm, attr) {
		var raw = elm[0];

		elm.bind('scroll', function() {
			console.log('scroll');
			if (raw.scrollTop + raw.offsetHeight >= raw.scrollHeight) {
				scope.$apply(attr.whenScrolled);
			}
		});
	};
});
