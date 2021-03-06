// Copyright (c) 2013 Intel Corporation. All rights reserved
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.xwalk.core.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import org.xwalk.core.HttpAuthDatabase;
import org.xwalk.core.R;
import org.xwalk.core.SslErrorHandler;
import org.xwalk.core.XWalkClient;
import org.xwalk.core.XWalkView;

public class XWalkDefaultClient extends XWalkClient {

    // Strings for displaying Dialog.
    private static String mAlertTitle;
    private static String mSslAlertTitle;
    private static String mOKButton;
    private static String mCancelButton;

    private Context mContext;
    private AlertDialog mDialog;
    private XWalkView mView;
    private HttpAuthDatabase mDatabase;

    public XWalkDefaultClient(Context context, XWalkView view) {
        mDatabase = HttpAuthDatabase.getInstance(context.getApplicationContext());
        mContext = context;
        mView = view;
    }

    /***
     * Retrieve the HTTP authentication username and password for a given
     * host & realm pair.
     *
     * @param host The host for which the credentials apply.
     * @param realm The realm for which the credentials apply.
     * @return String[] if found, String[0] is username, which can be null and
     *         String[1] is password. Return null if it can't find anything.
     */
    public String[] getHttpAuthUsernamePassword(String host, String realm) {
        return mDatabase.getHttpAuthUsernamePassword(host, realm);
    }

    @Override
    public void onReceivedError(XWalkView view, int errorCode,
            String description, String failingUrl) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setTitle(android.R.string.dialog_alert_title)
                .setMessage(description)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        mDialog = dialogBuilder.create();
        mDialog.show();
    }

    @Override
    public void onReceivedSslError(XWalkView view, SslErrorHandler handler,
            SslError error) {
        final SslErrorHandler sslHandler = handler;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setTitle(R.string.ssl_alert_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sslHandler.proceed();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sslHandler.cancel();
                        dialog.cancel();
                    }
                });
        mDialog = dialogBuilder.create();
        mDialog.show();
    }

    /***
     * Set the HTTP authentication credentials for a given host and realm.
     *
     * @param host The host for the credentials.
     * @param realm The realm for the credentials.
     * @param username The username for the password. If it is null, it means
     *                 password can't be saved.
     * @param password The password
     */
    public void setHttpAuthUsernamePassword(String host, String realm,
            String username, String password) {
        mDatabase.setHttpAuthUsernamePassword(host, realm, username, password);
    }
}
