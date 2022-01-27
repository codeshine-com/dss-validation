package com.codeshine.utils;

import java.io.File;

import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CommonCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.tsl.cache.CacheCleaner;
import eu.europa.esig.dss.tsl.function.OfficialJournalSchemeInformationURI;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.tsl.sync.AcceptAllStrategy;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.dss.xades.validation.XMLDocumentValidatorFactory;

public class Validator {
	private static final String LOTL_URL = "https://ec.europa.eu/tools/lotl/eu-lotl.xml";
	private static final String OJ_URL = "https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.2019.276.01.0001.01.ENG";

	public Reports validate(String documentPath) {
		CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();

		TLValidationJob job = job();
		TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();
		job.setTrustedListCertificateSource(trustedListsCertificateSource);
		job.onlineRefresh();

		commonCertificateVerifier.setTrustedCertSources(trustedListsCertificateSource);
		commonCertificateVerifier.setCrlSource(new OnlineCRLSource());
		commonCertificateVerifier.setOcspSource(new OnlineOCSPSource());
		commonCertificateVerifier.setAIASource(new DefaultAIASource());

		SignedDocumentValidator validator = new XMLDocumentValidatorFactory()
				.create(new FileDocument(documentPath));
		validator.setCertificateVerifier(commonCertificateVerifier);

		return validator.validateDocument();
	}

	public TLValidationJob job() {
		TLValidationJob job = new TLValidationJob();
		job.setOfflineDataLoader(offlineLoader());
		job.setOnlineDataLoader(onlineLoader());
		job.setTrustedListCertificateSource(trustedCertificateSource());
		job.setSynchronizationStrategy(new AcceptAllStrategy());
		job.setCacheCleaner(cacheCleaner());

		LOTLSource europeanLOTL = europeanLOTL();
		job.setListOfTrustedListSources(europeanLOTL);

		return job;
	}

	public TrustedListsCertificateSource trustedCertificateSource() {
		return new TrustedListsCertificateSource();
	}

	public LOTLSource europeanLOTL() {
		LOTLSource lotlSource = new LOTLSource();
		lotlSource.setUrl(LOTL_URL);
		lotlSource.setCertificateSource(new CommonCertificateSource());
		lotlSource.setSigningCertificatesAnnouncementPredicate(new OfficialJournalSchemeInformationURI(OJ_URL));
		lotlSource.setPivotSupport(true);

		return lotlSource;
	}

	public DSSFileLoader offlineLoader() {
		FileCacheDataLoader offlineFileLoader = new FileCacheDataLoader();
		offlineFileLoader.setCacheExpirationTime(Long.MAX_VALUE);
		offlineFileLoader.setDataLoader(new IgnoreDataLoader());
		offlineFileLoader.setFileCacheDirectory(tlCacheDirectory());

		return offlineFileLoader;
	}

	public DSSFileLoader onlineLoader() {
		FileCacheDataLoader onlineFileLoader = new FileCacheDataLoader();
		onlineFileLoader.setCacheExpirationTime(0);
		onlineFileLoader.setDataLoader(dataLoader());
		onlineFileLoader.setFileCacheDirectory(tlCacheDirectory());

		return onlineFileLoader;
	}

	public File tlCacheDirectory() {
		File rootFolder = new File(System.getProperty("java.io.tmpdir"));
		File tslCache = new File(rootFolder, "dss-tsl-loader");

		return tslCache;
	}

	public CommonsDataLoader dataLoader() {
		return new CommonsDataLoader();
	}

	public CacheCleaner cacheCleaner() {
		CacheCleaner cacheCleaner = new CacheCleaner();
		cacheCleaner.setCleanMemory(true);
		cacheCleaner.setCleanFileSystem(true);
		cacheCleaner.setDSSFileLoader(offlineLoader());

		return cacheCleaner;
	}

}