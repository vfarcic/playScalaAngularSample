describe('storiesModule controllers', function() {

    beforeEach(module('booksModule'));

    describe('booksCtrl controller', function() {

        var scope, httpBackend, element;
        var listBook1 = {"title":"Book1","link":"/api/v1/books/1"};
        var listBook2 = {"title":"Book2","link":"/api/v1/books/2"};
        var listBook3 = {"title":"Book3","link":"/api/v1/books/3"};
        var book1 = {
            id: 1,
            image: "Image1",
            title: "Book1",
            author: "Author1",
            price: 123.45,
            link: "Link1"
        };
        var ngTableParams;

        beforeEach(
            inject(function($injector, $controller, $httpBackend, $http, $rootScope, $compile) {
                httpBackend = $httpBackend;
                scope = $rootScope.$new();
                var ngTableParams;
                httpBackend.expectGET('/api/v1/books').respond([listBook1]);
                $controller('booksCtrl', {
                    $scope: scope,
                    $http: $http,
                    $ngTableParams: ngTableParams
                });
                spyOn(scope, 'setTableParams').and.callThrough();
                ngTableParams = jasmine.createSpy('ngTableParams');
                httpBackend.flush();
                element = $compile('<element>')(scope);
            })
        );

        describe('listBooks method', function() {
            it('should fetch the list of books from the server', function() {
                var books = [listBook1, listBook2];
                httpBackend.expectGET('/api/v1/books').respond(books);
                scope.listBooks();
                httpBackend.flush();
                expect(scope.books).toEqual(books);
            });
            it('should call the setTableParams function', function() {
                expect(scope.setTableParams).toHaveBeenCalled();
            });
        });

        describe('setTableParams function', function() {
            it('should be defined', function() {
                expect(scope.setTableParams).toBeDefined();
            });
        });

        describe('newBook method', function() {
            it('should set book to {}', function() {
                scope.newBook();
                expect(scope.book).toEqual({});
            });
            it('store a copy in originalBook', function() {
                expect(scope.originalBook).toEqual({});
            });
        });

        describe('saveBook method', function() {
            var books;
            beforeEach(function() {
                books = [listBook1, listBook2, listBook3];
                scope.book = listBook3;
                httpBackend.expectPUT('/api/v1/books').respond();
                httpBackend.expectGET('/api/v1/books').respond(books);
                scope.saveBook();
                httpBackend.flush();
            });
            it('should request PUT /api/v1/books', function() {
                // Do nothing. Code was moved to beforeEach
            });
            it('should fetch the new list of books from the server', function() {
                expect(scope.books).toEqual(books);
            });
            it('should reset book', function() {
                expect(scope.book).toEqual({});
            });
        });

        describe('openBook method', function() {
            beforeEach(
                function() {
                    httpBackend.expectGET('/api/v1/books/' + book1.id).respond(book1);
                    scope.openBook(book1.id);
                    httpBackend.flush();
                }
            );
            it('should set book', function() {
                expect(scope.book).toEqual(book1);
            });
            it('store a copy in originalBook', function() {
                expect(scope.originalBook).toEqual(book1);
            });
        });

        describe('revertBook method', function() {
           it('should copy originalBook to book', function() {
               scope.originalBook = book1;
               scope.revertBook();
               expect(scope.book).toEqual(book1);
           });
        });

        describe('deleteBook method', function() {
            var books;
            beforeEach(function() {
                scope.books = [listBook1, listBook2, listBook3];
                scope.book = listBook3;
                scope.book = book1;
                httpBackend.expectDELETE('/api/v1/books/' + scope.book.id).respond();
                httpBackend.expectGET('/api/v1/books').respond([listBook2, listBook3]);
                scope.deleteBook();
                httpBackend.flush();
            });
            it('should update books', function() {
                expect(scope.books).toEqual([listBook2, listBook3]);
            });
            it('should set book to {}', function() {
                expect(scope.book).toEqual({});
            });
        });

        describe('cssClass method', function() {
            it('should return has-error true and has-success false when ngModelController is invalid', function() {
                element.$invalid = true;
                element.$valid = false;
                expect(scope.cssClass(element)).toEqual({'has-error': true, 'has-success': false});
            });
            it('should return has-error false and has-success true when ngModelController is valid and dirty', function() {
                element.$invalid = false;
                element.$valid = true;
                expect(scope.cssClass(element)).toEqual({'has-error': false, 'has-success': true});
            });
        });

        describe('cssClassButton method', function() {
            it('should return btn-danger true and btn-success false when ngModelController is invalid', function() {
                element.$invalid = true;
                element.$valid = false;
                expect(scope.cssClassButton(element)).toEqual({'btn-danger': true, 'btn-success': false});
            });
            it('should return btn-danger false and btn-success true when ngModelController is valid and dirty', function() {
                element.$invalid = false;
                element.$valid = true;
                expect(scope.cssClassButton(element)).toEqual({'btn-danger': false, 'btn-success': true});
            });
        });

        describe('isValid method', function() {
            beforeEach(
                function() {
                    element.$invalid = false;
                    element.$valid = true;
                    scope.book = book1;
                    scope.originalBook = {};
                }
            );
            it('should return false when ngModelController is invalid', function() {
                element.$invalid = true;
                element.$valid = false;
                expect(scope.isValid(element)).toEqual(false);
            });
            it('should return false when book is unchanged', function() {
                scope.originalBook = book1;
                expect(scope.isValid(element)).toEqual(false)
            });
            it('should return true when ngModelController is valid and book is changed', function() {
                expect(scope.isValid(element)).toEqual(true)
            });
        });

        describe('canRevertBook method', function() {
            it('should return true when the book is different than the originalBook', function() {
                scope.book = book1;
                scope.originalBook = {};
                expect(scope.canRevertBook()).toEqual(true);
            });
            it('should return false when the book is the same as the originalBook', function() {
                scope.book = book1;
                scope.originalBook = book1;
                expect(scope.canRevertBook()).toEqual(false);
            });
        });

        describe('canDeleteBook method', function() {
            it('should return true when book.id is defined', function() {
                scope.book = book1;
                expect(scope.canDeleteBook()).toEqual(true);
            });
            it('should return false when book.id is undefined', function() {
                expect(scope.canDeleteBook()).toEqual(false);
            });
        });

        describe('pricePattern method', function() {
            it('should allow any number of digits followed with optional dot and one or more digits (i.e. 1223.45)', function() {
                expect('123.45').toMatch(scope.pricePattern());
                expect('123.').toMatch(scope.pricePattern());
                expect('123').toMatch(scope.pricePattern());
                expect('.45').not.toMatch(scope.pricePattern());
                expect('.').not.toMatch(scope.pricePattern());
            });
        });

        it('should call listBooks method', function() {
            expect(scope.books).toEqual([listBook1]);
        });

        it('should call newBook method', function() {
            expect(scope.book).toEqual({});
        });

    });

});