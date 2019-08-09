# Main Priorities

## I. Magicverse
1. Resolve the dynamic import of JSX code
2. Create Poc app using PCF (anchors)

## II. Make `react-native-magic-script` repo public (iOS & Android)
1. Minimum required
- Basic documentation explaining how to use the library
- Simple demo application
- Support for the following UI elements:
    - `<view>`
    - `<text>`
    - `<textEdit>`
    - `<button>`
    - `<image>`
    - `<scrollBar>`
    - `<scrollView>`
    - `<listView>`
    - `<listViewItem>`
    - `<model>`
    - `<dropdownList>`
    - `<dropdownListItem>`
    - `<toggle>`
    - `<video>`

- else?

2. Coding style guideline
- [Swift Style Guide](docs/swift_style_guide.md)
- [Kotlin Style Guide](docs/kotlin_style_guide.md)
3. Validators for components (JavaScript)

    The most proper validation is at the element level. This way the validator can evaluate the current value based on property description and previous value.

    The more I think about a general property validator the more I feel challenged by the SoC principle. I would like to have each element responsible for its own properties and preserve this responsibility within the element, avoiding splitting property validation between the element and the framework. That way it could be guaranteed there won't be discrepancy in property validation between different layers (sections) of the code.

4. Tests + CI
- unit tests on native side (iOS/Android)
- React Native app that lists all components
- QA
5. Proper repository management
- GitFlow (develop + feature branches)
- mandatory code review (Pull Requests)
- version management
6. Misc
- Should we open source an implementation of Lumin components for iOS/Android? Source code of Lumin components is not publicly available.
7. GitFlow & Project issues synchronization
- Create feature branches from master branch:
    - feature/#ISSUE_description
    - bugfix/#ISSUE_description
- Create pull request and assign reviewer
- Add image/video to the description of pull request if needed
- Reviewer should leave at least a summary comment in addition to other code comments
- Except the unusual situations, the pull request should be merged by the author of the Pull Request
