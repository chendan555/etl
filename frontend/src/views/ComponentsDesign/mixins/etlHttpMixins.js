import axios from '@/api/base/http';
const etlHttpMixins = {
  methods: {
    transferToFormData(params) {
      let formData = new FormData();
      for (let key in params) {
        formData.append(key, typeof params[key] === 'string' ? params[key] : JSON.stringify(
          params[key]));
      }
      let config = {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      };

      return {
        formData,
        config
      };
    },
    http(url, params) {
      if (!params) {
        return new Promise((resolve, reject) => {
          axios
            .post(url)
            .then(resolve)
            .catch(reject);
        });
      } else {
        const {
          formData,
          config
        } = this.transferToFormData(params);
        return new Promise((resolve, reject) => {
          axios
            .post(url, formData, config)
            .then(resolve)
            .catch(reject);
        });
      }
    },
  }
};
export default etlHttpMixins;
