// RSA 암호화 모듈 : 넥사크로 최적화(24.11.19)
this.RSAModule = function () {
  // Define a custom BigInteger implementation
  function BigInteger(value, base) {
    if (typeof value === 'string') {
      this.value = BigInt('0x' + value);
    } else if (typeof value === 'number' || typeof value === 'bigint') {
      this.value = BigInt(value);
    } else if (Array.isArray(value)) {
      this.value = value.reduce((acc, byte) => (acc << BigInt(8)) + BigInt(byte), BigInt(0));
    } else {
      console.log('Invalid value type for BigInteger');
      return null;
    }
  }

  BigInteger.prototype.modPowInt = function (e, m) {
    return new BigInteger((this.value ** BigInt(e)) % m.value);
  };

  BigInteger.prototype.toString = function (base) {
    return this.value.toString(base);
  };

  BigInteger.prototype.bitLength = function () {
    return this.value.toString(2).length;
  };

  // convert a (hex) string to a bignum object
  function parseBigInt(str, r) {
    return new BigInteger(str, r);
  }

  function byte2Hex(b) {
    return b.toString(16).padStart(2, '0');
  }

  // PKCS#1 (type 2, random) pad input string s to n bytes, and return a bigint
  function pkcs1pad2(s, n) {
    if (n < s.length + 11) {
      console.log("Message too long for RSA");
      return null;
    }
    var ba = new Uint8Array(n).fill(0);
    var i = s.length - 1;
    while (i >= 0 && n > 0) {
      var c = s.charCodeAt(i--);
      if (c < 128) {
        ba[--n] = c;
      } else if (c < 2048) {
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
    while (n > 2) {
      let randomByte = 0;
      while (randomByte === 0) {
        rng.nextBytes(ba.subarray(n - 1, n));
        randomByte = ba[n - 1];
      }
      ba[--n] = randomByte;
    }
    ba[--n] = 2;
    ba[--n] = 0;
    return new BigInteger(Array.from(ba), 256);
  }

  // RSA key constructor
  function RSAKey() {
    this.n = null;
    this.e = 0;
    this.d = null;
    this.p = null;
    this.q = null;
    this.dmp1 = null;
    this.dmq1 = null;
    this.coeff = null;
  }

  // Set the public key fields N and e from hex strings
  RSAKey.prototype.setPublic = function (N, E) {
    if (N && E && N.length > 0 && E.length > 0) {
      this.n = parseBigInt(N, 16);
      this.e = parseInt(E, 16);
    } else {
      console.log("암호화 키가 유효하지 않습니다.");
    }
  };

  // Perform raw public operation on "x": return x^e (mod n)
  RSAKey.prototype.doPublic = function (x) {
    if (!this.n || this.e === 0) {
      console.log("Public key not set");
      return null;
    }
    return x.modPowInt(this.e, this.n);
  };

  // Return the PKCS#1 RSA encryption of "text" as an even-length hex string
  RSAKey.prototype.encrypt = function (text) {
    var m = pkcs1pad2(text, (this.n.bitLength() + 7) >> 3);
    if (!m) return null;
    var c = this.doPublic(m);
    if (!c) return null;
    return c.toString(16).padStart(c.toString(16).length + (c.toString(16).length % 2), '0');
  };

  // SecureRandom 생성자 함수
  function SecureRandom() {}

  SecureRandom.prototype.nextBytes = function (ba) {
    var seed = secure_RandomSeed();
    for (var i = 0; i < ba.length; i++) {
      seed = (seed * 48271) % 2147483647; // Lehmer RNG for better randomness
      ba[i] = (seed % 256);
    }
  };

  // 보안 강화를 위한 시드 기반의 난수 생성 함수
  function secure_RandomSeed() {
    var seed = Date.now();
    var additionalEntropy = Math.floor(Math.random() * 1000000); // Add some additional entropy
    seed = (seed * 9301 + additionalEntropy) % 2147483647;
    return seed;
  }

  return new RSAKey();
};
