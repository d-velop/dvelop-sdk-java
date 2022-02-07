# DEPRECATED: d.velop cloud SDK for Java

<div align=center>
<img alt="No Maintenance" src="https://img.shields.io/maintenance/no/2022?style=for-the-badge">
</div>

This is the official SDK to build Apps for [d.velop cloud](https://www.d-velop.de/cloud/) using
the Java programming language.

## Usage

*For now, you'll have to check out this project from github and build/install locally. Were working on making these modules available via maven cental.*

In your projects `pom.xml`, include the d.velop cloud sdk as a dependency:

```xml
<dependencies>
    <dependency>
        <groupId>com.d-velop.sdk</groupId>
        <artifactId>dvelop-sdk-all</artifactId>
        <version>0.1.5</version>
    </dependency>
</dependencies>
```

Alternatively, you may include only the specific parts of the sdk you need:

```xml
<properties>
    <version.dvelopsdk>0.1.5</version.dvelopsdk>
</properties>

<dependencies>
    <dependency>
        <groupId>com.d-velop.sdk</groupId>
        <artifactId>dvelop-sdk-tenant</artifactId>
        <version>${version.dvelopsdk}</version>
    </dependency>

    <dependency>
        <groupId>com.d-velop.sdk</groupId>
        <artifactId>dvelop-sdk-idp</artifactId>
        <version>${version.dvelopsdk}</version>
    </dependency>
</dependencies>
```

More info on usage of sdk classes will come soon.

## Running the tests

Tests are run using [maven surefire](https://maven.apache.org)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct,
and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see
the [releases on this repository](https://github.com/d-velop/dvelop-sdk-java/releases).

## License

Please read [LICENSE](LICENSE) for licensing information.

## Acknowledgments

Thanks to the following projects for inspiration

* [Starting an Open Source Project](https://opensource.guide/starting-a-project/)
* [README template](https://gist.github.com/PurpleBooth/109311bb0361f32d87a2)
* [CONTRIBUTING template](https://github.com/nayafia/contributing-template/blob/master/CONTRIBUTING-template.md)