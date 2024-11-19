// RSA 암호화 모듈 : 엑스플랫폼 최적화 버전(24.11.19)
var RSAModule = function () {
  // Depends on jsbn.js and rng.js
  // Version 1.1: support utf-8 encoding in pkcs1pad2

  // Define a custom BigInteger implementation
  function BigInteger(value, base) {
    var instance = Object.create(BigInteger.prototype);
    
    if (typeof value === 'string') {
      instance.value = BigInt('0x' + value);
    } else if (typeof value === 'number' || typeof value === 'bigint') {
      instance.value = BigInt(value);
    } else if (Array.isArray(value)) {
      // Assume array represents bytes, convert to BigInt
      instance.value = BigInt(0);
      for (let i = 0; i < value.length; i++) {
        instance.value = (instance.value << BigInt(8)) + BigInt(value[i]);
      }
    } else {
      console.log('Invalid value type for BigInteger');
      return null;
    }
    return instance;
  }

  BigInteger.prototype.modPowInt = function (e, m) {
    return BigInteger((this.value ** BigInt(e)) % m.value);
  };

  BigInteger.prototype.toString = function (base) {
    return this.value.toString(base);
  };

  BigInteger.prototype.bitLength = function () {
    return this.value.toString(2).length;
  };

  // Convert a (hex) string to a BigInt object
  function parseBigInt(str, r) {
    return BigInteger(str, r);
  }

  function linebrk(s, n) {
    var ret = "";
    var i = 0;
    
    while (i + n < s.length) {
      ret += s.substring(i, i + n) + "\n";
      i += n;
    }
    return ret + s.substring(i, s.length);
  }

  function byte2Hex(b) {
    return b < 0x10 ? "0" + b.toString(16) : b.toString(16);
  }

  // PKCS#1 (type 2, random) pad input string s to n bytes, and return a BigInt
  function pkcs1pad2(s, n) {
    if (n < s.length + 11) {
      alert("Message too long for RSA");
      return null;
    }
    
    var ba = Array.from({ length: n }, () => 0);
    var i = s.length - 1;
    
    while (i >= 0 && n > 0) {
      var c = s.charCodeAt(i--);
      
      if (c < 128) {
        ba[--n] = c;
      } else if (c > 127 && c < 2048) {
        ba[--n] = (c & 63) | 128;
        ba[--n] = (c >> 6) | 192;
      } else {
        ba[--n] = (c & 63) | 128;
        ba[--n] = ((c >> 6) & 63) | 128;
        ba[--n] = (c >> 12) | 224;
      }
    }
    
    ba[--n] = 0;
    var rng = new SecureRandom();
    var x = Array.from({ length: 1 });
    
    while (n > 2) {
      x[0] = 0;
      while (x[0] == 0) rng.nextBytes(x);
      ba[--n] = x[0];
    }
    
    ba[--n] = 2;
    ba[--n] = 0;
    
    return BigInteger(ba, 256);
  }

  // "Empty" RSA key constructor
  function RSAKey() {
    var instance = Object.create(RSAKey.prototype);
    
    instance.n = null;
    instance.e = 0;
    instance.d = null;
    instance.p = null;
    instance.q = null;
    instance.dmp1 = null;
    instance.dmq1 = null;
    instance.coeff = null;
    
    return instance;
  }

  // Set the public key fields N and e from hex strings
  RSAKey.prototype.setPublic = function (N, E) {
    if (N != null && E != null && N.length > 0 && E.length > 0) {
      this.n = parseBigInt(N, 16);
      this.e = parseInt(E, 16);
    } else {
      console.log("암호화 키가 유효하지 않습니다.");
      location.href = "/goLogin.do";
    }
  };

  // Perform raw public operation on "x": return x^e (mod n)
  RSAKey.prototype.doPublic = function (x) {
    if (this.n == null || this.e == 0) {
      console.log("Public key not set");
      return null;
    }
    return x.modPowInt(this.e, this.n);
  };

  // Return the PKCS#1 RSA encryption of "text" as an even-length hex string
  RSAKey.prototype.encrypt = function (text) {
    var m = pkcs1pad2(text, (this.n.bitLength() + 7) >> 3);
    if (m == null) return null;
    
    var c = this.doPublic(m);
    if (c == null) return null;
    
    var h = c.toString(16);
    return (h.length & 1) == 0 ? h : "0" + h;
  };

  // SecureRandom constructor
  function SecureRandom() {
    return Object.create(SecureRandom.prototype);
  }

  SecureRandom.prototype.nextBytes = function (ba) {
    for (var i = 0; i < ba.length; i++) {
      ba[i] = Math.floor(256 * secure_Random());
    }
  };

  //[6월 TASK] 보안과제(2.9 적절하지 않은 난수 값 사용) 조치 ( 2020.06.05 )
  function secure_Random() {
    var seed = Date.now();
    seed = (seed * 9301 + 49297) % 233280;
    return seed / 233280.0;
  }

  return new RSAKey();
};
