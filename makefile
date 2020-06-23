LANG_FORMAT=node_modules/clang-format/bin/linux_x64/clang-format --style=Google
CSS_VALIDATOR=node_modules/css-validator/bin/css-validator
ESLINT=node_modules/eslint/bin/eslint.js
HTML_VALIDATE=node_modules/html-validate/bin/html-validate.js

node_modules:
	npm install clang-format css-validator html-validate eslint eslint-config-google

pretty: node_modules
	find capstone/src/main/java -iname *.java | xargs $(CLANG_FORMAT) -i
	find capstone/src/main/webapp -iname *.js | xargs $(CLANG_FORMAT) -i

validate: node_modules
	$(HTML_VALIDATE) capstone/src/main/webapp/*.html
	$(CSS_VALIDATOR) capstone/src/main/webapp/*.css
	$(ESLINT) capstone/src/main/webapp/*.js

package:
	mvn package
