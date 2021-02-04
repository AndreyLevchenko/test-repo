# NGINX Ingress Controller

*"NGINX Ingress Controller for Kubernetes https://kubernetes.github.io/ingress-nginx/"*

- [GitHub](https://github.com/kubernetes/ingress-nginx)
- [Helm chart](https://github.com/helm/charts/tree/master/stable/nginx-ingress)
- Kubernetes Concepts: [Ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/)

>NOTE: There are two versions of NGINX ingress, one by Nginx Inc and the other by the Kubernetes community. We are going to use the latter permutation.

## Installation

Conveniently, there is an official Helm chart *(we like)*. To install with Helm:

```console
$ helm install stable/nginx-ingress \
    --name nginx-ingress \
    --namespace ingress-nginx \
    --set controller.daemonset.useHostPort=true \
    --set controller.publishService.enabled=true \
    --set controller.kind=DaemonSet \
    --set controller.stats.enabled=true \
    --set controller.metrics.enabled=true \
    --set rbac.create=true \
    --tls
```

For automation, do `helm inspect stable/nginx-ingress > values.yaml` to get the default configuration and edit it as required. Here are the values as above:

```yaml
## nginx configuration
## Ref: https://github.com/kubernetes/ingress/blob/master/controllers/nginx/configuration.md
##
controller:
  name: controller

  ## Use host ports 80 and 443
  daemonset:
    useHostPort: true

  ## Allows customization of the external service
  ## the ingress will be bound to via DNS
  publishService:
    enabled: true

  ## DaemonSet or Deployment
  ##
  kind: DaemonSet

  stats:
    enabled: true

  ## If controller.stats.enabled = true and controller.metrics.enabled = true, Prometheus metrics will be exported
  ##
  metrics:
    enabled: true

## Enable RBAC as per https://github.com/kubernetes/ingress/tree/master/examples/rbac/nginx and https://github.com/kubernetes/ingress/issues/266
rbac:
  create: true
```

Then install with Helm:

```console
$ helm install stable/nginx-ingress \
    --name nginx-ingress \
    --namespace ingress-nginx \
    -f values.yaml \
    --tls
```

>WARNING: Using `HostPort` option which can cause conflict if port has already been assigned. `kubectl get pods -n ingress-nginx` will show `CrashLoopBackOff` in `STATUS`.

Check that the correct parameters have been set:

```console
$ helm status nginx-ingress --tls

```

Check that the pods are running:

```console
$ kubectl get pods -n ingress-nginx
```

## Test `nginx-ingress`

1. First, we need an HTTP service to put behind the ingress controller. [kubernetes/ingress-nginx](https://github.com/kubernetes/ingress-nginx) conveniently provides us one to use:

   ```console
   $ kubectl create -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/docs/examples/http-svc.yaml
   deployment "http-svc" created
   service "http-svc" created
   ```

2. Now we need an `Ingress` object:

   ```yaml
   apiVersion: extensions/v1beta1
   kind: Ingress
   metadata:
     name: ingress-test
     namespace: default
     # This is the annotation that makes use of the nginx-ingress-controller
     annotations:
       kubernetes.io/ingress.class: nginx
   spec:
     rules:
     - host: ingress-test.test.sohohousedigital.com
       http:
         paths:
         - path: /
           backend:
             # This assumes http-svc exists and routes to healthy endpoints
             serviceName: http-svc
             servicePort: 8080
   ```

3. We need to create a CNAME record for `Ingress` with the value of `spec.rules.host` that points to the `LoadBalancer Ingress`:

   ```console
   $ kubectl describe svc nginx-ingress-controller -n ingress-nginx
   ```

   If configured correctly, [ExternalDNS](external-dns.md) will use the `spec.rules.host` to configure Route 53. Otherwise, this can also be done is [Terraform](terraform.md):

   ```hcl
   resource "aws_route53_record" "ingress-test" {
     zone_id = "${aws_route53_zone.this.zone_id}"
     name    = "ingress-test"
     type    = "CNAME"
     ttl     = "60"
     records = ["${var.load_balancer_ingress}"]
   }
   ```

## TLS termaination

To use `nginx-ingress` for [TLS termination](https://github.com/kubernetes/ingress-nginx/tree/master/docs/examples/tls-termination), add the `spec.tls` block to the `Ingress` object:

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: ingress-test
  namespace: default
  annotations:
    kubernetes.io/ingress.class: nginx
spec:
  # This section is only required if TLS is to be enabled for the Ingress
  tls:
    - hosts:
      - ingress-test.test.sohohousedigital.com
      # This assumes tls-secret exists and the SSL
      # certificate contains a CN for foo.bar.com
      secretName: letsencrypt-stage-certificate
  rules:
    - host: ingress-test.test.sohohousedigital.com
      http:
        paths:
        - path: /
          backend:
            serviceName: http-svc
            servicePort: 8080
```

We can test that it is working with `curl` from [another pod](https://kubernetes.io/docs/tasks/debug-application-cluster/get-shell-running-container/):

```console
$ kubectl create -f https://k8s.io/examples/application/shell-demo.yaml
```

Here, we need to install curl first with `apt-get install curl`, then run the commands as described in these [docs](https://github.com/kubernetes/ingress-nginx/tree/master/docs/examples/tls-termination).
