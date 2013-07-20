/**
 * Loads the CMS Company Config tab
 */
var loadCompanyTab = function () {

    var url = "/html/portlet/ext/cmsconfig/company.jsp";
    var content = dijit.byId("companyTabContent");

    if (content) {
        content.destroyRecursive(false);
    }
    content = new dojox.layout.ContentPane({
        id: "companyTabContent"
    }).placeAt("companyTabContentDiv");

    content.attr("href", url);
    content.refresh();
};

/**
 * Saves the company basic information
 */
var saveCompanyBasicInfo = function () {

    //Getting the form values
    var companyPortalUrl = dijit.byId("companyPortalUrl").get("value");
    var companyMX = dijit.byId("companyMX").get("value");
    var companyEmailAddress = dijit.byId("companyEmailAddress").get("value");
    var bgColor = dijit.byId("bgColor").get("value");
    var bgURL = dijit.byId("bgURL").get("value");

    var xhrArgs = {
        url: "/DotAjaxDirector/com.dotmarketing.portlets.cmsmaintenance.ajax.CMSConfigAjax/cmd/saveCompanyBasicInfo",
        content: {
            'portalURL': companyPortalUrl,
            'mx': companyMX,
            'emailAddress': companyEmailAddress,
            'size': bgColor,
            'homeURL': bgURL
        },
        handleAs: "json",
        load: function (data) {

            var isError = false;
            if (data.success == false || data.success == "false") {
                isError = true;
            }

            showDotCMSSystemMessage(data.message, isError);
        },
        error: function (error) {
            showDotCMSSystemMessage(error, true);
        }
    };
    dojo.xhrPost(xhrArgs);
};

/**
 * Saves the company locale information
 */
var saveCompanyLocaleInfo = function () {

    //Getting the form values
    var companyLanguageId = dijit.byId("companyLanguageId").get("value");
    var companyTimeZoneId = dijit.byId("companyTimeZoneId").get("value");

    var xhrArgs = {
        url: "/DotAjaxDirector/com.dotmarketing.portlets.cmsmaintenance.ajax.CMSConfigAjax/cmd/saveCompanyLocaleInfo",
        content: {
            'languageId': companyLanguageId,
            'timeZoneId': companyTimeZoneId
        },
        handleAs: "json",
        load: function (data) {

            var isError = false;
            if (data.success == false || data.success == "false") {
                isError = true;
            }

            showDotCMSSystemMessage(data.message, isError);
        },
        error: function (error) {
            showDotCMSSystemMessage(error, true);
        }
    };
    dojo.xhrPost(xhrArgs);
};

/**
 * Saves the company authentication type
 */
var saveCompanyAuthTypeInfo = function () {

    //Getting the form values
    var companyAuthType = dijit.byId("companyAuthType").get("value");

    var xhrArgs = {
        url: "/DotAjaxDirector/com.dotmarketing.portlets.cmsmaintenance.ajax.CMSConfigAjax/cmd/saveCompanyAuthTypeInfo",
        content: {
            'authType': companyAuthType
        },
        handleAs: "json",
        load: function (data) {

            var isError = false;
            if (data.success == false || data.success == "false") {
                isError = true;
            }

            showDotCMSSystemMessage(data.message, isError);
        },
        error: function (error) {
            showDotCMSSystemMessage(error, true);
        }
    };
    dojo.xhrPost(xhrArgs);
};

/**
 * Loads the CMS Remote Publishing Config tab
 */
var loadRemotePublishingTab = function () {

    var url = "/html/portlet/ext/cmsconfig/remotePublishing.jsp";
    var content = dijit.byId("remotePublishingTabContent");

    if (content) {
        content.destroyRecursive(false);
    }
    content = new dojox.layout.ContentPane({
        id: "remotePublishingTabContent"
    }).placeAt("remotePublishingTabContentDiv");

    content.attr("href", url);
    content.refresh();
};
