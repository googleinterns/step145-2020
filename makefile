CLANG_FORMAT=node_modules/clang-format/bin/linux_x64/clang-format --style=Google
CSS_VALIDATOR=node_modules/css-validator/bin/css-validator
ESLINT=node_modules/eslint/bin/eslint.js
HTML_VALIDATE=node_modules/html-validate/bin/html-validate.js
PRETTIER=node_modules/prettier/bin-prettier.js

node_modules:
	npm install prettier clang-format css-validator html-validate eslint eslint-config-google

devprep: 
	cp src/main/webapp/vendor/startbootstrap/index.html src/main/webapp/index.html

devserver: devprep
	mvn package appengine:run

deploy: devprep
	mvn package appengine:deploy

pretty: node_modules
	$(PRETTIER) src/main/webapp/*.css --write
	find src/main/java -iname **.java | xargs $(CLANG_FORMAT) -i
	find src/test/java -iname **.java | xargs $(CLANG_FORMAT) -i
	find src/main/webapp -iname **.js | xargs $(CLANG_FORMAT) -i

validate: node_modules
	$(HTML_VALIDATE) src/main/webapp/*.html
	$(CSS_VALIDATOR) src/main/webapp/*.css
	$(ESLINT) src/main/webapp/*.js
